package com.alex.lowcodingplatform.ai.factory;

import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.ai.service.AiService;
import com.alex.lowcodingplatform.ai.tool.FileWriteTool;
import com.alex.lowcodingplatform.ai.tool.ToolManager;
import com.alex.lowcodingplatform.exception.BusinessException;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.service.ChatHistoryService;
import com.alex.lowcodingplatform.utils.SpringContextUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final Cache<String, AiService> SERVICE_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                // Handle cache removal if needed
                log.debug("AI 服务被移除, appId:{}, cause:{}", key, cause);
            })
            .build();


    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private ToolManager toolManager;

    @Bean
    public AiService aiService() {
        return createAiServiceWithMemory(0L, CodeGenerateType.HTML);
    }

    /**
     * 获取AI服务实例
     * @param appId
     * @return
     */
    public AiService getAiService(Long appId, CodeGenerateType type) {
        // 从本地缓存获取服务实例，不存在再创建
        String cacheKey = buildCacheKey(appId, type);
        return SERVICE_CACHE.get(cacheKey, key -> createAiServiceWithMemory(appId, type));
    }

    /**
     * 工厂模式创建AiService
     * @param appId
     * @return
     */
    public AiService createAiServiceWithMemory(Long appId, CodeGenerateType type) {
        log.debug("AppId: [{}] 创建AI服务实例", appId);
        // 根据AppId创建会话记忆
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(50)
                .build();
        // 加载会话历史到内存
        chatHistoryService.loadHistoryToMemory(appId, memory, 20);

        // 不同类型的使用不同的模型
        return switch (type) {
            case VUE_PROJECT -> {
                // 获取一个复杂任务推理模型
                StreamingChatModel reasoningStreamingChatModel =
                        SpringContextUtil.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiService.class)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> memory)
                        .tools(toolManager.getToolList())
                        // 防止工具幻觉问题
                        .hallucinatedToolNameStrategy(request ->
                                ToolExecutionResultMessage.from(request, "Error: there is no tool called " + request.name())
                        )
                        .build();
            }

            case HTML, MULTI_FILE -> {
                // 获取一个简单的模型
                ChatModel chatModel =
                        SpringContextUtil.getBean("simpleChatModelPrototype", ChatModel.class);
                StreamingChatModel streamingChatModel =
                        SpringContextUtil.getBean("simpleStreamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(streamingChatModel)
                        .chatMemoryProvider(memoryId -> memory)
                        .build();
            }

            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + type.getValue());
        };
    }

    private String buildCacheKey(long appId, CodeGenerateType type) {
        return appId + "_" + type.getValue();
    }
}
