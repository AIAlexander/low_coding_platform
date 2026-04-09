package com.alex.lowcodingplatform.ai.service;

import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wangshuhao
 * @date 2026/4/9
 */
@SpringBootTest
@Slf4j
class AiRoutingServiceTest {

    @Autowired
    private AiRoutingService aiRoutingService;


    @Test
    void route() {
        String prompt = "做一个简单个人介绍页面";
        CodeGenerateType route = aiRoutingService.route(prompt);
        log.info("用户需求：{}， 分类结果：{}", prompt, route);
        String prompt2 = "做一个公司官网，需要首页、关于我们、联系我们三个页面";
        CodeGenerateType route1 = aiRoutingService.route(prompt2);
        log.info("用户需求：{}， 分类结果：{}", prompt2, route1);
        String prompt3 = "做一个电商管理系统，包含用户管理，商品管理，订单管理，需要路由和状态管理";
        CodeGenerateType route2 = aiRoutingService.route(prompt3);
        log.info("用户需求：{}， 分类结果：{}", prompt3, route2);
    }
}