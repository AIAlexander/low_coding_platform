package com.alex.lowcodingplatform.ai.service;

import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * @author wsh
 * @date 2026/4/5
 */
public interface AiService {


    String chat(String userMessage);

    /**
     * 生成 HTML 代码
     * @param userMessage       用户msg
     * @return
     */
    @SystemMessage(fromResource = "prompt/html_system_prompt.txt")
    HtmlCodeResponse generateHtmlCode(@UserMessage String userMessage);

    @SystemMessage(fromResource = "prompt/multi_html_system_prompt.txt")
    MultiHtmlCodeResponse generateMultiHtmlCode(String userMessage);

    /**
     * 生成 HTML 代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/html_system_prompt.txt")
    Flux<String> generateHtmlCodeStream(@MemoryId long appId, @UserMessage String userMessage);

    /**
     * 生成多文件代码（流式）
     *
     * @param userMessage 用户消息
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/multi_html_system_prompt.txt")
    Flux<String> generateMultiFileCodeStream(@MemoryId long appId, @UserMessage String userMessage);

    /**
     * 生成Vue项目代码（流式）
     * @param appId
     * @param userMessage
     * @return 使用TokenStream进行返回
     */
    @SystemMessage(fromResource = "prompt/vue_project_system_prompt.txt")
    TokenStream generateVueProjectStream(@MemoryId long appId, @UserMessage String userMessage);
}
