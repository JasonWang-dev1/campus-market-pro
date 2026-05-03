package com.campusmarket.agent;

import com.campusmarket.agent.model.AgentResponse;
import com.campusmarket.agent.model.TaskPlan;
import com.campusmarket.agent.model.TaskStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 核心服务
 * <p>
 * 编排完整的 Agent 工作流：
 * <ol>
 *   <li>接收用户自然语言指令</li>
 *   <li>Planner 调用 AI 拆解为多步骤任务</li>
 *   <li>Executor 依次执行每个步骤（支持链式依赖）</li>
 *   <li>汇总执行结果，返回给用户</li>
 * </ol>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final Planner planner;
    private final Executor executor;

    /**
     * 执行用户指令（完整流程）
     *
     * @param prompt 用户自然语言指令
     * @param userId 当前登录用户 ID
     * @return 包含规划 + 执行结果的响应
     */
    public AgentResponse execute(String prompt, Long userId) {
        log.info("===== Agent 开始执行 =====");
        log.info("指令: {}", prompt);

        // Step 1: 规划
        TaskPlan plan = planner.plan(prompt, userId);
        List<TaskStep> steps = plan.getSteps();

        // Step 2: 执行
        int total = steps.size();
        int success = 0;
        int failed = 0;
        String lastResult = null;

        // 记录已完成步骤（用于链式依赖）
        Map<Integer, String> stepResults = new ConcurrentHashMap<>();

        for (int i = 0; i < total; i++) {
            TaskStep step = steps.get(i);

            try {
                // 检查依赖
                if (step.getDependsOn() != null) {
                    String depResult = stepResults.get(step.getDependsOn());
                    if (depResult == null) {
                        throw new IllegalStateException(
                                "依赖的步骤 #" + step.getDependsOn() + " 未执行或无结果");
                    }
                    // 将前置结果注入当前步骤参数
                    step.getParams().put("_previous_step_result", depResult);
                }

                // 执行
                String result = executor.execute(step, lastResult);
                step.setResult(result);
                step.setStatus("success");
                stepResults.put(step.getOrder(), result);
                lastResult = result;
                success++;

            } catch (Exception e) {
                log.error("步骤 #{} 执行失败: {}", step.getOrder(), e.getMessage());
                step.setStatus("failed");
                step.setError(e.getMessage());
                failed++;

                // 如果某一步失败，标记后续依赖步骤为跳过
                markDependentStepsSkipped(steps, i);
                break; // 默认中断执行（可根据需要改为继续）
            }
        }

        // Step 3: 汇总
        String status = failed == 0 ? "success"
                : (success > 0 ? "partial" : "failed");

        String summaryMessage = buildSummary(prompt, success, failed, steps);

        log.info("===== Agent 执行完毕: status={}, success={}, failed={} =====",
                status, success, failed);

        return AgentResponse.builder()
                .prompt(prompt)
                .summary(plan.getSummary())
                .steps(steps)
                .status(status)
                .successCount(success)
                .failedCount(failed)
                .summaryMessage(summaryMessage)
                .build();
    }

    /**
     * 标记依赖步骤为跳过
     */
    private void markDependentStepsSkipped(List<TaskStep> steps, int failedIndex) {
        TaskStep failedStep = steps.get(failedIndex);
        for (int j = failedIndex + 1; j < steps.size(); j++) {
            TaskStep next = steps.get(j);
            if (next.getDependsOn() != null
                    && next.getDependsOn().equals(failedStep.getOrder())) {
                next.setStatus("skipped");
                next.setError("前置步骤 #" + failedStep.getOrder() + " 失败，跳过");
            }
        }
    }

    /**
     * 构建汇总消息
     */
    private String buildSummary(String prompt, int success, int failed,
                                List<TaskStep> steps) {
        StringBuilder sb = new StringBuilder();
        sb.append("指令「").append(prompt).append("」");

        if (failed == 0) {
            sb.append("已全部完成");
        } else if (success > 0) {
            sb.append("已完成 ").append(success).append(" 步，")
                    .append(failed).append(" 步失败");
        } else {
            sb.append("执行失败");
        }

        sb.append("，共 ").append(steps.size()).append(" 步");
        return sb.toString();
    }
}
