package com.alex.lowcodingplatform.service.impl;

import cn.hutool.json.JSONUtil;
import com.alex.lowcodingplatform.service.BuildSseService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangshuhao
 * @date 2026/4/10
 */
@Service
@Slf4j
public class BuildSseServiceImpl implements BuildSseService {

    private final Cache<Long, Sinks.Many<String>> sinkCache = Caffeine.newBuilder()
            .maximumSize(2000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .build();

    @Override
    public Flux<String> subscribe(Long appId) {
        return getOrCreate(appId).asFlux();
    }

    @Override
    public void publishBuilding(Long appId) {
        log.info("send Vue building....");
        publish(appId, "building", null, null, false);
    }

    @Override
    public void publishSuccess(Long appId, String projectPath) {
        log.info("send Vue build success....");
        publish(appId, "success", projectPath, null, true);
    }

    @Override
    public void publishFail(Long appId, String projectPath, String errorMsg) {
        log.info("send Vue build fail....");
        publish(appId, "fail", projectPath, errorMsg, true);
    }

    private void publish(long appId, String status, String projectPath, String errorMessage, boolean complete) {
        Sinks.Many<String> sink = getOrCreate(appId);

        Map<String, Object> payload = new HashMap<>();
        payload.put("appId", appId);
        payload.put("status", status);
        payload.put("ts", System.currentTimeMillis());
        if (projectPath != null) {
            payload.put("projectPath", projectPath);
        }
        if (errorMessage != null) {
            payload.put("errorMessage", errorMessage);
        }

        sink.tryEmitNext(JSONUtil.toJsonStr(payload));

        if (complete) {
            sink.tryEmitComplete();
        }
    }

    private Sinks.Many<String> getOrCreate(long appId) {
        return sinkCache.get(appId, key -> Sinks.many().replay().limit(1));
    }
}
