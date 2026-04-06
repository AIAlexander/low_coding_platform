package com.alex.lowcodingplatform.ai.factory;

import com.alex.lowcodingplatform.ai.service.AiService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wsh
 * @date 2026/4/5
 */

@Configuration
public class AiServiceFactory {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    @Bean
    public AiService aiService() {
        return AiServices.builder(AiService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
