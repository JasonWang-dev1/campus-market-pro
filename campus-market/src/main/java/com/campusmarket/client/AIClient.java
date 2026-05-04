package com.campusmarket.client;

/**
 * AI 客户端统一接口
 * <p>
 * 屏蔽不同 AI 服务提供商（DeepSeek / MiMo / Ollama）的差异，
 * 提供统一的对话调用能力。
 * </p>
 */
public interface AIClient {

    /**
     * 普通对话，返回文本响应
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI 生成的文本
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * JSON 模式对话，强制 AI 返回 JSON 格式
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI 返回的 JSON 字符串
     */
    String chatJson(String systemPrompt, String userPrompt);

    /**
     * JSON 模式对话，自动反序列化为指定类型
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @param clazz        目标类型
     * @return 反序列化后的对象
     */
    <T> T chatJson(String systemPrompt, String userPrompt, Class<T> clazz);
}
