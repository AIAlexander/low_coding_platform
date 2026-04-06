package com.alex.lowcodingplatform.ai.core.saver;

import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;

/**
 * @author wsh
 * @date 2026/4/5
 */
public class HtmlCodeSaver extends AbstractSaver<HtmlCodeResponse>{
    @Override
    protected CodeGenerateType generateType() {
        return CodeGenerateType.HTML;
    }

    @Override
    protected void saveFile(HtmlCodeResponse result, String baseDir) {
        writeFile(baseDir, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validate(HtmlCodeResponse result) {
        super.validate(result);
        ThrowUtils.throwIf(result.getHtmlCode() == null, ErrorCode.PARAMS_ERROR, "生成的HTML代码为空");
    }
}
