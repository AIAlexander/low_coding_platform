package com.alex.lowcodingplatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wsh
 * @date 2026/4/6
 */
@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    private static final long serialVersionUID = 1L;
}

