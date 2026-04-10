package com.alex.lowcodingplatform.ratelimit;

/**
 * @author wangshuhao
 * @date 2026/4/10
 */
public enum RateLimitType {

    /**
     * 接口级别限流
     */
    API,

    /**
     * 用户级别限流
     */
    USER,

    /**
     * IP级别限流
     */
    IP
}
