package com.campusmarket.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 任务步骤
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStep {

    /** 执行序号 */
    private int order;

    /** 工具名称（对应 Tool.getName()） */
    private String tool;

    /** 工具参数 */
    private Map<String, Object> params;

    /** 步骤描述 */
    private String description;

    /** 依赖的上一步序号（null 表示无依赖） */
    private Integer dependsOn;

    /** 执行结果（由 Executor 填充） */
    private String result;

    /** 执行状态：pending / success / failed */
    @Builder.Default
    private String status = "pending";

    /** 错误信息 */
    private String error;
}
