package com.campusmarket.agent;

import com.campusmarket.common.BusinessException;
import com.campusmarket.common.ResultCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * DeepSeek API 客户端
 * <p>
 * 专供 Agent 系统调用 DeepSeek 大模型，使用独立的 API Key 和配置。
 * 支持普通对话和 JSON 模式响应。
 * </p>
 */
@Slf4j
@Service
public class DeepSeekClient {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-base-url}")
    private String apiBaseUrl;

    @Value("${deepseek.model}")
    private String model;

    @Value("${deepseek.temperature:0.1}")
    private double temperature;

    @Value("${deepseek.max-tokens:4096}")
    private int maxTokens;

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        log.info("DeepSeek 客户端初始化完成: baseUrl={}, model={}", apiBaseUrl, model);
    }

    /**
     * 调用 DeepSeek 对话 API，返回文本响应
     */
    public String chat(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, false);
    }

    /**
     * 调用 DeepSeek 对话 API，强制返回 JSON 格式
     */
    public String chatJson(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, true);
    }

    /**
     * 调用 DeepSeek 对话 API，解析返回的 JSON 为指定类型
     */
    public <T> T chatJson(String systemPrompt, String userPrompt, Class<T> clazz) {
        String json = doChat(systemPrompt, userPrompt, true);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("DeepSeek JSON 解析失败, response={}", json, e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 响应解析失败");
        }
    }

    /**
     * 核心请求方法
     */
    private String doChat(String systemPrompt, String userPrompt, boolean jsonMode) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("stream", false);

        // JSON 模式
        if (jsonMode) {
            ObjectNode jsonSchema = objectMapper.createObjectNode();
            jsonSchema.put("type", "json_object");
            requestBody.set("response_format", jsonSchema);
        }

        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject()
                .put("role", "system")
                .put("content", systemPrompt);
        messages.addObject()
                .put("role", "user")
                .put("content", userPrompt);

        String body = requestBody.toString();

        Request request = new Request.Builder()
                .url(apiBaseUrl + "/chat/completions")
                .post(RequestBody.create(body, JSON_MEDIA))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .build();

        long start = System.currentTimeMillis();
        try (Response response = httpClient.newCall(request).execute()) {
            long elapsed = System.currentTimeMillis() - start;
            log.debug("DeepSeek 调用完成, 耗时={}ms", elapsed);

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                log.error("DeepSeek API 错误: status={}, body={}", response.code(), errorBody);
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 服务暂时不可用");
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            return extractContent(responseBody);

        } catch (IOException e) {
            log.error("DeepSeek 请求异常", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 服务连接失败");
        }
    }

    /**
     * 从 OpenAI 兼容格式响应中提取 content 字段
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
                log.warn("DeepSeek 返回空内容: {}", responseBody);
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 返回内容为空");
            }
            return content.trim();
        } catch (Exception e) {
            if (e instanceof BusinessException) throw (BusinessException) e;
            log.error("解析 DeepSeek 响应失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 响应解析失败");
        }
    }
}
