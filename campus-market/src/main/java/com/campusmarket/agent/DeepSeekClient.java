package com.campusmarket.agent;

import com.campusmarket.client.AIClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * DeepSeek API 客户端（薄代理）
 * <p>
 * 委托给统一的 AIClient 实现，保留此类以维持现有注入兼容。
 * Planner、AIDescriptionTool 等组件仍可直接注入此类使用。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekClient {

    private final AIClient aiClient;

    /**
     * 调用对话 API，返回文本响应
     */
    public String chat(String systemPrompt, String userPrompt) {
        return aiClient.chat(systemPrompt, userPrompt);
    }

    /**
     * 调用对话 API，强制返回 JSON 格式
     */
    public String chatJson(String systemPrompt, String userPrompt) {
        return aiClient.chatJson(systemPrompt, userPrompt);
    }

    /**
     * 调用对话 API，解析返回的 JSON 为指定类型
     */
    public <T> T chatJson(String systemPrompt, String userPrompt, Class<T> clazz) {
        return aiClient.chatJson(systemPrompt, userPrompt, clazz);
    }
}
