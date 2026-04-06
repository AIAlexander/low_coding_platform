package com.alex.lowcodingplatform.ai.core.parser.executor;

import com.alex.lowcodingplatform.ai.core.parser.HtmlCodeParser;
import com.alex.lowcodingplatform.ai.core.parser.MultiFileCodeParser;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.exception.BusinessException;
import com.alex.lowcodingplatform.exception.ErrorCode;

/**
 * @author wsh
 * @date 2026/4/5
 */
public class CodeParserExecutor {

    private static final HtmlCodeParser HTML_CODE_PARSER = new HtmlCodeParser();

    private static final MultiFileCodeParser MULTI_FILE_CODE_PARSER = new MultiFileCodeParser();

    public static Object parser(String codeContent, CodeGenerateType type) {
        return switch (type) {
            case HTML -> HTML_CODE_PARSER.parseCode(codeContent);
            case MULTI_FILE -> MULTI_FILE_CODE_PARSER.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        };
    }
}
