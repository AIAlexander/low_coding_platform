package com.alex.lowcodingplatform.ai.facade;

import com.alex.lowcodingplatform.ai.core.parser.executor.CodeParserExecutor;
import com.alex.lowcodingplatform.ai.core.saver.executor.CodeSaverExecutor;
import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.ai.service.AiService;
import com.alex.lowcodingplatform.exception.BusinessException;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;
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

    @Autowired
    private AiService aiService;


    public File generateCode(String userMessage, CodeGenerateType type) {
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        return switch (type) {
            case HTML -> {
                HtmlCodeResponse htmlCodeResponse = aiService.generateHtmlCode(userMessage);
                yield CodeSaverExecutor.saveCode(htmlCodeResponse, type);
            }
            case MULTI_FILE -> {
                MultiHtmlCodeResponse response = aiService.generateMultiHtmlCode(userMessage);
                yield CodeSaverExecutor.saveCode(response, type);
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型");
        };
    }

    public Flux<String> generateCodeAndSaveStream(String userMessage, CodeGenerateType type) {
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        return switch (type) {
            case HTML -> {
                Flux<String> htmlCodeStream = aiService.generateHtmlCodeStream(userMessage);
                yield generateCodeStream(htmlCodeStream, type);
            }
            case MULTI_FILE -> {
                Flux<String> multiFileCodeStream = aiService.generateMultiFileCodeStream(userMessage);
                yield generateCodeStream(multiFileCodeStream, type);
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型");
        };
    }

    private Flux<String> generateCodeStream(Flux<String> codeStream, CodeGenerateType type) {
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
                File file = CodeSaverExecutor.saveCode(parserResult, type);
                log.info("保存成功，目录:{}", file.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败, {}", e.getMessage());
            }
        });

    }
}
