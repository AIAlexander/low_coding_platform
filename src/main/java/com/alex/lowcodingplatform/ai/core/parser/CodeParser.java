package com.alex.lowcodingplatform.ai.core.parser;

/**
 * @author wsh
 * @date 2026/4/5
 */
public interface CodeParser<T> {

    /**
     * 解析代码内容
     *
     * @param codeContent 原始代码内容
     * @return 解析后的结果对象
     */
    T parseCode(String codeContent);
}

