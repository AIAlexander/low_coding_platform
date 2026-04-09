package com.alex.lowcodingplatform.ai.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由模型配置
 * 
 * @author wangshuhao
 * @date 2026/4/9
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.routing-chat-model")
@Data
public class RoutingModelConfig {
    
    private String baseUrl;
    private String apiKey;
    private String modelName;
    private Boolean logRequests;
    private Boolean logResponses;
    private Integer maxTokens;
    private Integer maxRetries;
    
    /**
     * 创建路由专用的 ChatModel Bean
     * 使用 @Qualifier("routingChatModel") 进行区分
     */
    @Bean("routingChatModel")
    public ChatModel routingChatModel() {
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .maxTokens(maxTokens)
                .maxRetries(maxRetries);
        return builder.build();
    }
}
