package com.campusmarket.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 任务规划
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPlan {

    /** 原始指令 */
    private String prompt;

    /** 规划摘要 */
    private String summary;

    /** 任务步骤列表 */
    private List<TaskStep> steps;
}
