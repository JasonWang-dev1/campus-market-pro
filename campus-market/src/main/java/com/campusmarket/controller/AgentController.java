package com.campusmarket.controller;

import com.campusmarket.agent.AgentService;
import com.campusmarket.agent.model.AgentRequest;
import com.campusmarket.agent.model.AgentResponse;
import com.campusmarket.common.BusinessException;
import com.campusmarket.common.Result;
import com.campusmarket.common.ResultCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Agent 智能体控制器
 * <p>
 * 接收用户自然语言指令，由 Agent 系统自动规划并执行。
 * </p>
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/execute")
    public Result<AgentResponse> execute(@Valid @RequestBody AgentRequest request) {
        Long userId = getCurrentUserId();
        request.setUserId(userId);

        AgentResponse response = agentService.execute(request.getPrompt(), userId);
        return Result.success(response);
    }

    /**
     * 从 SecurityContext 获取当前用户 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Long.valueOf(authentication.getPrincipal().toString());
    }
}
