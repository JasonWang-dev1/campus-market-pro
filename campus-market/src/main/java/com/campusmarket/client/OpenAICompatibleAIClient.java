package com.campusmarket.client;

import com.campusmarket.common.BusinessException;
import com.campusmarket.common.ResultCode;
import com.campusmarket.config.AIClientProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * OpenAI 兼容格式的 AI 客户端实现
 * <p>
 * 支持 DeepSeek、MiMo、Ollama 等所有 OpenAI 兼容 API。
 * 内置超时控制、自动重试（指数退避）。
 * </p>
 */
@Slf4j
public class OpenAICompatibleAIClient implements AIClient {

    private final AIClientProperties props;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    public OpenAICompatibleAIClient(AIClientProperties props) {
        this.props = props;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(props.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(props.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        log.info("AIClient 初始化完成: provider={}, baseUrl={}, model={}",
                props.getProvider(), props.getBaseUrl(), props.getModel());
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, false);
    }

    @Override
    public String chatJson(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, true);
    }

    @Override
    public <T> T chatJson(String systemPrompt, String userPrompt, Class<T> clazz) {
        String json = doChat(systemPrompt, userPrompt, true);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("AI JSON 反序列化失败, response={}", json, e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 响应解析失败");
        }
    }

    /**
     * 核心请求方法（含重试）
     */
    private String doChat(String systemPrompt, String userPrompt, boolean jsonMode) {
        int maxAttempts = props.getRetry().getMaxAttempts();
        long backoffMs = props.getRetry().getBackoffMs();
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return executeRequest(systemPrompt, userPrompt, jsonMode);
            } catch (BusinessException e) {
                // 业务异常（如 401 认证失败）不重试
                throw e;
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    long waitMs = backoffMs * (1L << (attempt - 1)); // 指数退避
                    log.warn("AI 调用失败 (第{}/{}次), {}ms 后重试: {}",
                            attempt, maxAttempts, waitMs, e.getMessage());
                    try {
                        Thread.sleep(waitMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 服务调用被中断");
                    }
                }
            }
        }

        log.error("AI 调用重试 {} 次后仍然失败", maxAttempts, lastException);
        throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 服务连接失败，请检查网络或服务状态");
    }

    /**
     * 执行单次 HTTP 请求
     */
    private String executeRequest(String systemPrompt, String userPrompt, boolean jsonMode) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", props.getModel());
        requestBody.put("temperature", props.getTemperature());
        requestBody.put("max_tokens", props.getMaxTokens());
        requestBody.put("stream", false);

        if (jsonMode) {
            ObjectNode jsonSchema = objectMapper.createObjectNode();
            jsonSchema.put("type", "json_object");
            requestBody.set("response_format", jsonSchema);
        }

        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject().put("role", "system").put("content", systemPrompt);
        messages.addObject().put("role", "user").put("content", userPrompt);

        Request.Builder builder = new Request.Builder()
                .url(props.getBaseUrl() + "/chat/completions")
                .post(RequestBody.create(requestBody.toString(), JSON_MEDIA))
                .header("Content-Type", "application/json");

        // apiKey 为空时不添加 Authorization（兼容 Ollama 本地模型）
        if (props.getApiKey() != null && !props.getApiKey().isBlank()) {
            builder.header("Authorization", "Bearer " + props.getApiKey());
        }

        Request request = builder.build();

        long start = System.currentTimeMillis();
        try (Response response = httpClient.newCall(request).execute()) {
            long elapsed = System.currentTimeMillis() - start;
            log.debug("AI 调用完成: provider={}, 耗时={}ms, status={}",
                    props.getProvider(), elapsed, response.code());

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                log.error("AI API 错误: status={}, body={}", response.code(), errorBody);

                // 4xx 客户端错误不重试（401/403/400 等）
                if (response.code() >= 400 && response.code() < 500) {
                    String msg = response.code() == 401 || response.code() == 403
                            ? "AI 服务认证失败，请检查 API Key"
                            : "AI 请求参数错误: " + extractErrorMessage(errorBody);
                    throw new BusinessException(ResultCode.INTERNAL_ERROR, msg);
                }
                // 5xx 服务端错误可重试
                throw new IOException("AI API 返回 " + response.code());
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            return extractContent(responseBody);

        } catch (IOException e) {
            throw new RuntimeException("AI 请求异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从错误响应中提取错误消息
     */
    private String extractErrorMessage(String errorBody) {
        try {
            JsonNode root = objectMapper.readTree(errorBody);
            return root.path("error").path("message").asText(errorBody);
        } catch (Exception e) {
            return errorBody;
        }
    }

    /**
     * 从 OpenAI 兼容格式响应中提取 content
     */
    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            if (content == null || content.isBlank()) {
                log.warn("AI 返回空内容: {}", responseBody);
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 返回内容为空");
            }
            return content.trim();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析 AI 响应失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 响应解析失败");
        }
    }
}
