package com.alex.lowcodingplatform.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author wsh
 * @date 2026/4/5
 */

@Data
@Description("多个文件代码生成结果")
public class MultiHtmlCodeResponse {

    @Description("生成代码的描述")
    private String description;

    @Description("生成的HTML代码")
    private String htmlCode;

    @Description("生成的CSS代码")
    private String cssCode;

    @Description("生成的JavaScript代码")
    private String jsCode;
}
