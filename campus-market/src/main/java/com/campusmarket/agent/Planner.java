package com.campusmarket.agent;

import com.campusmarket.agent.model.TaskPlan;
import com.campusmarket.agent.model.TaskStep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 任务规划器
 * <p>
 * 接收用户自然语言指令，调用 DeepSeek 模型分析意图，
 * 生成结构化的多步骤任务计划。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Planner {

    private final DeepSeekClient deepSeekClient;
    private final ToolRegistry toolRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析用户指令，生成任务计划
     *
     * @param prompt 用户自然语言指令
     * @param userId 当前用户 ID
     * @return 结构化的任务计划
     */
    public TaskPlan plan(String prompt, Long userId) {
        log.info("开始规划任务: prompt={}, userId={}", prompt, userId);

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(prompt, userId);

        String jsonResponse = deepSeekClient.chatJson(systemPrompt, userPrompt);

        TaskPlan plan = parsePlan(jsonResponse, prompt);
        log.info("任务规划完成: 共 {} 个步骤", plan.getSteps().size());

        return plan;
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt() {
        return """
                你是一个校园二手交易平台的智能助手，负责将用户的自然语言指令拆解为可执行的任务步骤。

                ## 可用工具
                %s

                ## 输出格式
                你必须严格按以下 JSON 格式返回，不要包含任何其他文字：

                {
                  "summary": "对用户任务的简要概括",
                  "steps": [
                    {
                      "order": 1,
                      "tool": "工具名称",
                      "params": { "参数名": "参数值" },
                      "description": "这一步要做什么",
                      "depends_on": null
                    }
                  ]
                }

                ## 规则
                1. steps 是数组，至少包含 1 个步骤
                2. order 从 1 开始递增
                3. depends_on 填写依赖的上一步 order 值（无依赖填 null）
                4. params 中的参数必须与工具定义一致
                5. 如果用户指令不明确，默认执行商品搜索
                """.formatted(toolRegistry.getToolDescriptions());
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(String prompt, Long userId) {
        return "用户指令: " + prompt + "\n当前用户ID: " + userId;
    }

    /**
     * 解析 DeepSeek 返回的 JSON 为 TaskPlan
     */
    private TaskPlan parsePlan(String json, String originalPrompt) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String summary = root.path("summary").asText("处理用户指令: " + originalPrompt);
            JsonNode stepsNode = root.path("steps");

            List<TaskStep> steps = new ArrayList<>();
            if (stepsNode.isArray()) {
                Iterator<JsonNode> elements = stepsNode.elements();
                while (elements.hasNext()) {
                    JsonNode stepNode = elements.next();
                    TaskStep step = parseStep(stepNode);
                    if (step != null) {
                        steps.add(step);
                    }
                }
            }

            if (steps.isEmpty()) {
                log.warn("Planner 返回空步骤，使用默认搜索步骤");
                steps.add(createDefaultStep(originalPrompt));
            }

            return TaskPlan.builder()
                    .prompt(originalPrompt)
                    .summary(summary)
                    .steps(steps)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("解析规划 JSON 失败: json={}", json, e);
            // 降级：返回默认的搜索步骤
            List<TaskStep> fallback = List.of(createDefaultStep(originalPrompt));
            return TaskPlan.builder()
                    .prompt(originalPrompt)
                    .summary("搜索相关商品")
                    .steps(fallback)
                    .build();
        }
    }

    /**
     * 解析单个步骤
     */
    @SuppressWarnings("unchecked")
    private TaskStep parseStep(JsonNode stepNode) {
        try {
            int order = stepNode.path("order").asInt();
            String tool = stepNode.path("tool").asText();
            String description = stepNode.path("description").asText("");
            int dependsOn = stepNode.path("depends_on").isNull()
                    ? 0 : stepNode.path("depends_on").asInt();

            if (tool.isBlank()) {
                log.warn("步骤 {} 缺少工具名，跳过", order);
                return null;
            }

            // 解析 params
            Map<String, Object> params = objectMapper.convertValue(
                    stepNode.path("params"), Map.class);

            return TaskStep.builder()
                    .order(order)
                    .tool(tool)
                    .params(params)
                    .description(description)
                    .dependsOn(dependsOn > 0 ? dependsOn : null)
                    .status("pending")
                    .build();

        } catch (Exception e) {
            log.error("解析步骤失败: stepNode={}", stepNode, e);
            return null;
        }
    }

    /**
     * 创建默认的搜索步骤（降级方案）
     */
    private TaskStep createDefaultStep(String keyword) {
        return TaskStep.builder()
                .order(1)
                .tool("search_products")
                .params(Map.of("keyword", keyword))
                .description("搜索商品: " + keyword)
                .status("pending")
                .build();
    }
}
