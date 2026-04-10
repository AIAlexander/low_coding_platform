package com.alex.lowcodingplatform.ai.tool;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangshuhao
 * @date 2026/4/9
 *
 * 工具管理器
 *
 */

@Slf4j
@Component
public class ToolManager {

    private static final Map<String, BaseTool> TOOL_MAP = new HashMap<>();

    @Resource
    private BaseTool[] baseTools;

    @PostConstruct
    public void init() {
        for (BaseTool baseTool : baseTools) {
            TOOL_MAP.put(baseTool.getToolName(), baseTool);
            log.info("注册工具： {} -> {}", baseTool.getToolName(), baseTool.getDisplayName());
        }
        log.info("ToolManager init, tool size: {}", TOOL_MAP.size());
    }

    public BaseTool getTool(String toolName) {
        return TOOL_MAP.get(toolName);
    }

    public BaseTool[] getToolList() {
        return baseTools;
    }
}
