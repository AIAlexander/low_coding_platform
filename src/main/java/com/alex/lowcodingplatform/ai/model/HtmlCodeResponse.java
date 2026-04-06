package com.alex.lowcodingplatform.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author wsh
 * @date 2026/4/5
 */

@Data
@Description("HTML代码返回响应")
public class HtmlCodeResponse {

    @Description("生成代码的描述")
    private String description;

    @Description("生成的HTML代码")
    private String htmlCode;

}
