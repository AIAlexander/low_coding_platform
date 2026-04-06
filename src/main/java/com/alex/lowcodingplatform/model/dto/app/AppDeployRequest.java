package com.alex.lowcodingplatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wsh
 * @date 2026/4/6
 */
@Data
public class AppDeployRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 应用ID
     */
    private Long appId;
}
