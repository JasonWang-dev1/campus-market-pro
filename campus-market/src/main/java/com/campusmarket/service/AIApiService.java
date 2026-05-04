package com.campusmarket.service;

import com.campusmarket.client.AIClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI API 调用服务
 * <p>
 * 委托给统一的 AIClient，用于商品描述生成等场景。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIApiService {

    private final AIClient aiClient;

    /**
     * 根据商品标题生成描述
     *
     * @param title        商品标题
     * @param requirements 额外要求（可选）
     * @return 生成的描述文本
     */
    public String generateDescription(String title, String requirements) {
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

        return aiClient.chat(systemPrompt, userPrompt.toString());
    }
}
