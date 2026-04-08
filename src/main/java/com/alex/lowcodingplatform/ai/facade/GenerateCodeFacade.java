package com.alex.lowcodingplatform.ai.facade;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alex.lowcodingplatform.ai.core.parser.executor.CodeParserExecutor;
import com.alex.lowcodingplatform.ai.core.saver.executor.CodeSaverExecutor;
import com.alex.lowcodingplatform.ai.factory.AiServiceFactory;
import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.ai.model.message.AiResponseMessage;
import com.alex.lowcodingplatform.ai.model.message.ToolExecutedMessage;
import com.alex.lowcodingplatform.ai.model.message.ToolRequestMessage;
import com.alex.lowcodingplatform.ai.service.AiService;
import com.alex.lowcodingplatform.exception.BusinessException;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @author wsh
 * @date 2026/4/5
 */

@Service
@Slf4j
public class GenerateCodeFacade {

    @Resource
    private AiServiceFactory aiServiceFactory;

    public File generateCode(String userMessage, CodeGenerateType type, Long appId) {
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        AiService aiService = aiServiceFactory.getAiService(appId, type);
        return switch (type) {
            case HTML -> {
                HtmlCodeResponse htmlCodeResponse = aiService.generateHtmlCode(userMessage);
                yield CodeSaverExecutor.saveCode(htmlCodeResponse, type, appId);
            }
            case MULTI_FILE -> {
                MultiHtmlCodeResponse response = aiService.generateMultiHtmlCode(userMessage);
                yield CodeSaverExecutor.saveCode(response, type, appId);
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型");
        };
    }

    public Flux<String> generateCodeAndSaveStream(String userMessage, CodeGenerateType type, Long appId) {
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        AiService aiService = aiServiceFactory.getAiService(appId, type);
        return switch (type) {
            case HTML -> {
                Flux<String> htmlCodeStream = aiService.generateHtmlCodeStream(appId, userMessage);
                yield generateCodeStream(htmlCodeStream, type, appId);
            }
            case MULTI_FILE -> {
                Flux<String> multiFileCodeStream = aiService.generateMultiFileCodeStream(appId, userMessage);
                yield generateCodeStream(multiFileCodeStream, type, appId);
            }
            case VUE_PROJECT -> {
                TokenStream vueProjectCodeStream = aiService.generateVueProjectStream(appId, userMessage);
                yield processTokenStream(vueProjectCodeStream);
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型");
        };
    }

    private Flux<String> generateCodeStream(Flux<String> codeStream, CodeGenerateType type, Long appId) {
        // 接受输出流
        StringBuilder stringBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            stringBuilder.append(chunk);
        }).doOnComplete(() -> {
            try {
                // 流式返回结束，解析结果并存入文件
                String result = stringBuilder.toString();
                // 解析
                Object parserResult = CodeParserExecutor.parser(result, type);
                // 存入文件
                File file = CodeSaverExecutor.saveCode(parserResult, type, appId);
                log.info("保存成功，目录:{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败, {}", e.getMessage());
            }
        });

    }

    /**
     * VUE_PROJECT使用TokenStream，整个方法返回的是Flux
     * 因此开发一个适配器，适配Flux
     * @return
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                // 普通的AI响应，封装成一个AiResponseMessage对象，方便解析
                AiResponseMessage aiMsg = new AiResponseMessage(partialResponse);
                sink.next(JSONUtil.toJsonStr(aiMsg));
            }
            ).onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                // AI调用工具请求的参数，封装成一个ToolRequestMessage
                ToolRequestMessage toolRequestMsg = new ToolRequestMessage(toolExecutionRequest);
                sink.next(JSONUtil.toJsonStr(toolRequestMsg));
            }
            ).onToolExecuted((toolExecution) -> {
                // AI调用工具完成的参数，封装成一个ToolExecutedMessage
                ToolExecutedMessage toolExecutedMsg = new ToolExecutedMessage(toolExecution);
                sink.next(JSONUtil.toJsonStr(toolExecutedMsg));
            }).onCompleteResponse((ChatResponse response) -> {
                // AI 响应完成
                sink.complete();
            }).onError((Throwable error) -> {
                // 处理错误
                error.printStackTrace();
                sink.error(error);
            }).start();
        });
    }
}
