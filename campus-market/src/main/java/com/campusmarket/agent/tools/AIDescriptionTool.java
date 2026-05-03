package com.campusmarket.agent.tools;

import com.campusmarket.agent.DeepSeekClient;
import com.campusmarket.agent.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AI 商品描述生成工具
 * <p>
 * 调用 DeepSeek 模型，根据商品标题和额外要求生成商品描述文案。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIDescriptionTool implements Tool {

    private final DeepSeekClient deepSeekClient;

    @Override
    public String getName() {
        return "generate_description";
    }

    @Override
    public String getDescription() {
        return "AI 智能生成商品描述文案，根据标题和额外要求生成适合校园交易的描述";
    }

    @Override
    public String getParameterDescription() {
        return """
                {
                  "title": "商品标题（必填）",
                  "requirements": "额外要求（可选，如：强调成色/使用时长）"
                }""";
    }

    @Override
    public String execute(Map<String, Object> params) {
        String title = paramStr(params, "title", "");
        if (title.isBlank()) {
            return "错误：商品标题不能为空";
        }

        String requirements = paramStr(params, "requirements", null);

        String systemPrompt = """
                你是一个校园二手交易平台的商品描述撰写助手。
                请根据商品标题生成一段简洁、吸引人的商品描述。

                要求：
                1. 描述商品的核心特点和使用场景
                2. 说明商品的成色和使用情况（合理推测）
                3. 提供合理的交易建议（面交/邮寄）
                4. 语言亲切自然，适合校园场景
                5. 控制在 200 字以内
                """;

        StringBuilder userPrompt = new StringBuilder("商品标题：" + title);
        if (requirements != null && !requirements.isBlank()) {
            userPrompt.append("\n额外要求：").append(requirements);
        }

        log.info("AI 生成描述: title={}", title);

        String result = deepSeekClient.chat(systemPrompt, userPrompt.toString());

        return String.format("""
                【AI 生成描述】

                %s

                ---
                提示：你可以将此描述用于发布商品，或要求重新生成。
                """, result);
    }

    private String paramStr(Map<String, Object> params, String key, String defaultValue) {
        Object val = params.get(key);
        return val != null ? val.toString() : defaultValue;
    }
}
