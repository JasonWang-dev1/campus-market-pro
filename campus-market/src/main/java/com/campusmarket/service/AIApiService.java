package com.campusmarket.service;

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
 * AI API 调用服务
 */
@Slf4j
@Service
public class AIApiService {

    @Value("${ai.api-base-url}")
    private String apiBaseUrl;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.temperature}")
    private double temperature;

    @Value("${ai.max-tokens}")
    private int maxTokens;

    private OkHttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        log.info("AI 服务初始化完成: baseUrl={}, model={}", apiBaseUrl, model);
    }

    /**
     * 根据商品标题生成描述
     *
     * @param title        商品标题
     * @param requirements 额外要求（可选）
     * @return 生成的描述文本
     */
    public String generateDescription(String title, String requirements) {
        // 构建提示词
        String systemPrompt = """
                你是一个校园二手交易平台的商品描述撰写助手。
                请根据商品标题生成一段简洁、吸引人的商品描述。
                要求：
                1. 描述商品的核心特点和使用场景
                2. 说明商品的成色和使用情况
                3. 提供合理的交易建议（面交/邮寄）
                4. 语言亲切自然，适合校园场景
                5. 控制在 200 字以内
                """;

        StringBuilder userPrompt = new StringBuilder("商品标题：" + title);
        if (requirements != null && !requirements.isBlank()) {
            userPrompt.append("\n额外要求：").append(requirements);
        }

        return callAI(systemPrompt, userPrompt.toString());
    }

    /**
     * 调用 AI API
     */
    private String callAI(String systemPrompt, String userPrompt) {
        // 构建请求体
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("stream", false);

        ArrayNode messages = requestBody.putArray("messages");
        messages.addObject()
                .put("role", "system")
                .put("content", systemPrompt);
        messages.addObject()
                .put("role", "user")
                .put("content", userPrompt);

        Request request = new Request.Builder()
                .url(apiBaseUrl + "/chat/completions")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .header("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("AI API 调用失败: status={}, body={}", response.code(), response.body());
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 服务暂时不可用");
            }

            String responseBody = response.body() != null ? response.body().string() : "";
            return parseResponse(responseBody);

        } catch (IOException e) {
            log.error("AI API 请求异常", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 服务连接失败，请检查网络或服务状态");
        }
    }

    /**
     * 解析 OpenAI 兼容格式的响应
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content");
            if (content.isMissingNode() || content.isNull()) {
                log.warn("AI 响应格式异常: {}", responseBody);
                throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 响应格式异常");
            }
            return content.asText().trim();
        } catch (Exception e) {
            log.error("解析 AI 响应失败", e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI 响应解析失败");
        }
    }
}
