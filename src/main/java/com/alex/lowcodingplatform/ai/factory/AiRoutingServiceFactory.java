package com.alex.lowcodingplatform.ai.factory;

import com.alex.lowcodingplatform.ai.service.AiRoutingService;
import com.alex.lowcodingplatform.utils.SpringContextUtil;
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

    public AiRoutingService createRoutingService() {
        // 从容器中获取多例的模型
        ChatModel routingChatModel = SpringContextUtil.getBean("routingChatModel", ChatModel.class);
        return AiServices.builder(AiRoutingService.class)
                .chatModel(routingChatModel)
                .build();
    }

    @Bean
    public AiRoutingService aiRoutingService() {
        return createRoutingService();
    }
}
