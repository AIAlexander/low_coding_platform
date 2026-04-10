package com.alex.lowcodingplatform.service;

import reactor.core.publisher.Flux;

/**
 * @author wangshuhao
 * @date 2026/4/10
 */
public interface BuildSseService {

    Flux<String> subscribe(Long appId);

    void publishBuilding(Long appId);

    void publishSuccess(Long appId, String projectPath);

    void publishFail(Long appId, String projectPath, String errorMessage);
}
