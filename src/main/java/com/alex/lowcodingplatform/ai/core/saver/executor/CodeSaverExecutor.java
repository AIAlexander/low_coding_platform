package com.alex.lowcodingplatform.ai.core.saver.executor;

import com.alex.lowcodingplatform.ai.core.saver.HtmlCodeSaver;
import com.alex.lowcodingplatform.ai.core.saver.MultiHtmlCodeSaver;
import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.exception.BusinessException;
import com.alex.lowcodingplatform.exception.ErrorCode;

import java.io.File;

/**
 * @author wsh
 * @date 2026/4/5
 */
public class CodeSaverExecutor {

    private static final HtmlCodeSaver HTML_SAVER = new HtmlCodeSaver();

    private static final MultiHtmlCodeSaver MULTI_HTML_SAVER = new MultiHtmlCodeSaver();


    public static File saveCode(Object result, CodeGenerateType type) {
        return switch (type) {
            case HTML -> HTML_SAVER.save((HtmlCodeResponse) result);
            case MULTI_FILE -> MULTI_HTML_SAVER.save((MultiHtmlCodeResponse) result);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的类型");
        };
    }


}
