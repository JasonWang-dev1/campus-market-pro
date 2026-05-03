package com.campusmarket.agent;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 工具注册中心
 * <p>
 * 自动收集所有 {@link Tool} 接口的实现 Bean，提供按名称查找能力。
 * 新增工具只需实现 Tool 接口并标注 {@code @Component}，无需手动注册。
 * </p>
 */
@Slf4j
@Component
public class ToolRegistry {

    private final Map<String, Tool> toolMap = new ConcurrentHashMap<>();

    public ToolRegistry(List<Tool> tools) {
        for (Tool tool : tools) {
            toolMap.put(tool.getName(), tool);
        }
    }

    @PostConstruct
    public void init() {
        log.info("Agent 工具注册完成，可用工具: [{}]",
                String.join(", ", toolMap.keySet()));
    }

    /**
     * 按名称获取工具
     */
    public Tool getTool(String name) {
        Tool tool = toolMap.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("未找到工具: " + name
                    + "，可用工具: " + String.join(", ", toolMap.keySet()));
        }
        return tool;
    }

    /**
     * 判断工具是否存在
     */
    public boolean hasTool(String name) {
        return toolMap.containsKey(name);
    }

    /**
     * 获取全部工具
     */
    public List<Tool> getAllTools() {
        return List.copyOf(toolMap.values());
    }

    /**
     * 获取工具清单描述（供 AI 规划时参考）
     */
    public String getToolDescriptions() {
        return toolMap.values().stream()
                .map(t -> String.format("- %s: %s\n  参数: %s",
                        t.getName(), t.getDescription(), t.getParameterDescription()))
                .collect(Collectors.joining("\n"));
    }
}
