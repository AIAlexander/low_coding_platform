package com.alex.lowcodingplatform.ai.factory;

import com.alex.lowcodingplatform.ai.service.AiRoutingService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangshuhao
 * @date 2026/4/9
 *
 * 智能路由服务工厂
 *
 */
@Configuration
public class AiRoutingServiceFactory {

    /**
     * 注入路由专用的 ChatModel
     * 使用 @Qualifier 指定 Bean 名称
     */
    @Resource
    @Qualifier("routingChatModel")
    private ChatModel routingChatModel;

    @Bean
    public AiRoutingService aiRoutingService() {
        return AiServices.builder(AiRoutingService.class)
                .chatModel(routingChatModel)
                .build();
    }
}
