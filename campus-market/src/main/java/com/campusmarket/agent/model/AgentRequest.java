package com.campusmarket.agent.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Agent 请求体
 */
@Data
public class AgentRequest {

    /** 用户输入的自然语言指令 */
    @NotBlank(message = "指令不能为空")
    private String prompt;

    /** 当前登录用户 ID（从 JWT 中自动获取） */
    private Long userId;
}
