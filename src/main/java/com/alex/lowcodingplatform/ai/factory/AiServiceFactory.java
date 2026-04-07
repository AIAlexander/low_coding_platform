package com.alex.lowcodingplatform.ai.factory;

import com.alex.lowcodingplatform.ai.service.AiService;
import com.alex.lowcodingplatform.service.ChatHistoryService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author wsh
 * @date 2026/4/5
 */

@Configuration
@Slf4j
public class AiServiceFactory {

    /**
     * AI服务的本地缓存
     * 因为AI服务绑定App Id，防止频繁创建AI服务，故放入本地缓存
     */
    private final Cache<Long, AiService> SERVICE_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                // Handle cache removal if needed
                log.debug("AI 服务被移除, appId:{}, cause:{}", key, cause);
            })
            .build();

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Bean
    public AiService aiService() {
        return createAiServiceWithMemory(0L);
    }

    /**
     * 获取AI服务实例
     * @param appId
     * @return
     */
    public AiService getAiService(Long appId) {
        // 从本地缓存获取服务实例，不存在再创建
        return SERVICE_CACHE.get(appId, this::createAiServiceWithMemory);
    }

    /**
     * 工厂模式创建AiService
     * @param appId
     * @return
     */
    public AiService createAiServiceWithMemory(Long appId) {
        log.debug("AppId: [{}] 创建AI服务实例", appId);
        // 根据AppId创建会话记忆
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 加载会话历史到内存
        chatHistoryService.loadHistoryToMemory(appId, memory, 20);
        return AiServices.builder(AiService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(memory)
                .build();
    }
}
