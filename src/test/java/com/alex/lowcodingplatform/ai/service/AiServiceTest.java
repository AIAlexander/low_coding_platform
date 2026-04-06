package com.alex.lowcodingplatform.ai.service;

import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wsh
 * @date 2026/4/5
 */
@SpringBootTest
class AiServiceTest {

    @Autowired
    private AiService aiService;

    @Test
    void generateHtmlCode() {

        HtmlCodeResponse response = aiService.generateHtmlCode("做一个简单的任务记录网站，不超过50行代码");
        Assertions.assertNotNull(response);

    }

    @Test
    void generateMultiHtmlCode() {

        MultiHtmlCodeResponse response = aiService.generateMultiHtmlCode("做一个简单的任务记录网站，不超过50行代码");
        Assertions.assertNotNull(response);
    }
}