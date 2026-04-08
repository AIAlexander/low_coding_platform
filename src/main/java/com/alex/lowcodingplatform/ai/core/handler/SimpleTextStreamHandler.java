package com.alex.lowcodingplatform.ai.core.handler;

import cn.hutool.core.util.StrUtil;
import com.alex.lowcodingplatform.model.entity.User;
import com.alex.lowcodingplatform.model.enums.ChatHistoryMessageTypeEnum;
import com.alex.lowcodingplatform.service.ChatHistoryService;
import reactor.core.publisher.Flux;

/**
 * @author wangshuhao
 * @date 2026/4/8
 */
public class SimpleTextStreamHandler {

    /**
     * 简单的文本流式处理
     * @param stream
     * @param chatHistoryService
     * @param appId
     * @param loginUser
     * @return
     */
    public Flux<String> handle(Flux<String> stream,
                               ChatHistoryService chatHistoryService,
                               Long appId,
                               User loginUser) {
        StringBuilder responseBuilder = new StringBuilder();
        return stream
                .map(chunk -> {
                    // 收集 AI 的返回内容
                    responseBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // AI流式返回结束，添加AI消息到历史记录
                    String aiResponse = responseBuilder.toString();
                    if (StrUtil.isNotBlank(aiResponse)) {
                        chatHistoryService.addChatMessage(appId, aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                    }
                }).doOnError(error -> {
                    // AI 回复失败，记录信息
                    String errorMsg = "AI回复失败: " + error.getMessage();
                    chatHistoryService.addChatMessage(appId, errorMsg, ChatHistoryMessageTypeEnum.AI.getValue(), loginUser.getId());
                });
    }


}
