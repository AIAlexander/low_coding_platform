package com.alex.lowcodingplatform.service;

import com.alex.lowcodingplatform.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.alex.lowcodingplatform.model.entity.ChatHistory;
import com.alex.lowcodingplatform.model.entity.User;
import com.alex.lowcodingplatform.model.enums.ChatHistoryMessageTypeEnum;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * @author wangshuhao
 * @date 2026/4/7
 */
public interface ChatHistoryService extends IService<ChatHistory> {


    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    boolean clearChatHistory(Long appId);

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    int loadHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

}
