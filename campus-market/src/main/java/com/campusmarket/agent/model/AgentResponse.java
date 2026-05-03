package com.campusmarket.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent 响应体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {

    /** 原始指令 */
    private String prompt;

    /** 规划摘要 */
    private String summary;

    /** 任务步骤及执行结果 */
    private List<TaskStep> steps;

    /** 总体状态：success / partial / failed */
    private String status;

    /** 成功步骤数 */
    private int successCount;

    /** 失败步骤数 */
    private int failedCount;

    /** 综合摘要 */
    private String summaryMessage;
}
