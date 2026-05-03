package com.campusmarket.agent;

import com.campusmarket.agent.model.TaskStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 任务执行器
 * <p>
 * 根据规划好的任务步骤，调用对应的工具执行。
 * 支持链式依赖：一个步骤可依赖前置步骤的结果。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Executor {

    private final ToolRegistry toolRegistry;

    /**
     * 执行单个任务步骤
     *
     * @param step           待执行的步骤
     * @param previousResult 上一步的执行结果（用于链式传递）
     * @return 执行结果文本
     */
    public String execute(TaskStep step, String previousResult) {
        String toolName = step.getTool();
        log.info("执行步骤 #{}: tool={}, desc={}", step.getOrder(), toolName, step.getDescription());

        try {
            Tool tool = toolRegistry.getTool(toolName);

            // 如果依赖于上一步，将上一步结果注入参数
            if (step.getDependsOn() != null && previousResult != null) {
                step.getParams().put("_previous_result", previousResult);
            }

            long start = System.currentTimeMillis();
            String result = tool.execute(step.getParams());
            long elapsed = System.currentTimeMillis() - start;

            log.info("步骤 #{} 执行成功, 耗时={}ms", step.getOrder(), elapsed);
            return result;

        } catch (Exception e) {
            log.error("步骤 #{} 执行失败: tool={}, error={}",
                    step.getOrder(), toolName, e.getMessage(), e);
            throw e;
        }
    }
}
