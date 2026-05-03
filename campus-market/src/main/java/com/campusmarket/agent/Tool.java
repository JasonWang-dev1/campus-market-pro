package com.campusmarket.agent;

import java.util.Map;

/**
 * Agent 工具接口
 * <p>
 * 所有可被 Agent 调用的工具必须实现此接口。
 * 工具通过 {@code @Component} 注册到 Spring 容器，
 * 由 {@link ToolRegistry} 统一管理。
 * </p>
 */
public interface Tool {

    /**
     * 工具唯一标识名（供 Planner 引用）
     */
    String getName();

    /**
     * 工具描述（供 AI 理解工具用途）
     */
    String getDescription();

    /**
     * 工具参数说明（JSON Schema 格式描述，供 AI 生成参数时参考）
     */
    String getParameterDescription();

    /**
     * 执行工具
     *
     * @param params 参数 Map
     * @return 执行结果文本
     */
    String execute(Map<String, Object> params);
}
