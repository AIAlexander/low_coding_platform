package com.alex.lowcodingplatform.ai.service;

import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * @author wangshuhao
 * @date 2026/4/9
 *
 * AI 智能路由
 *
 */
public interface AiRoutingService {

    /**
     * AI做分类任务
     * 使用结构化输出类型
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/routing-system-prompt.txt")
    CodeGenerateType route(@UserMessage String userMessage);

}
