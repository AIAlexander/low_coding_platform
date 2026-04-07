package com.alex.lowcodingplatform.ai.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @author wsh
 * @date 2026/4/5
 */
@Getter
public enum CodeGenerateType {

    HTML("原生 HTML 模式", "html"),
    MULTI_FILE("原生多文件模式", "multi_file"),
    VUE_PROJECT("Vue 项目模式", "vue_project");

    private final String text;
    private final String value;

    CodeGenerateType(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static CodeGenerateType getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (CodeGenerateType anEnum : CodeGenerateType.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}

