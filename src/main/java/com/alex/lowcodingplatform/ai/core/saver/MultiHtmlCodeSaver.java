package com.alex.lowcodingplatform.ai.core.saver;

import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;

/**
 * @author wsh
 * @date 2026/4/5
 */
public class MultiHtmlCodeSaver extends AbstractSaver<MultiHtmlCodeResponse>{
    @Override
    protected CodeGenerateType generateType() {
        return CodeGenerateType.MULTI_FILE;
    }

    @Override
    protected void saveFile(MultiHtmlCodeResponse result, String baseDir) {
        // html代码
        writeFile(baseDir, "index.html", result.getHtmlCode());
        // css代码
        writeFile(baseDir, "style.css", result.getCssCode());
        // js代码
        writeFile(baseDir, "script.js", result.getJsCode());
    }

    @Override
    protected void validate(MultiHtmlCodeResponse result) {
        super.validate(result);
        ThrowUtils.throwIf(result.getHtmlCode() == null, ErrorCode.PARAMS_ERROR, "生成的HTML代码为空");
        ThrowUtils.throwIf(result.getCssCode() == null, ErrorCode.PARAMS_ERROR, "生成的CSS代码为空");
        ThrowUtils.throwIf(result.getJsCode() == null, ErrorCode.PARAMS_ERROR, "生成的JavaScript代码为空");
    }
}
