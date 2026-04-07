package com.alex.lowcodingplatform.ai.service;

import com.alex.lowcodingplatform.ai.factory.AiServiceFactory;
import com.alex.lowcodingplatform.ai.model.HtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.MultiHtmlCodeResponse;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import jakarta.annotation.Resource;
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

    @Resource
    private AiServiceFactory factory;

    @Test
    void generateHtmlCode() {
        AiService aiService = factory.getAiService(0L, CodeGenerateType.HTML);
        HtmlCodeResponse response = aiService.generateHtmlCode("做一个简单的任务记录网站，不超过20行代码");
        Assertions.assertNotNull(response);

        HtmlCodeResponse response2 = aiService.generateHtmlCode("不需要生成网站代码！你只需要告诉我刚才开发了一个什么网站");
        Assertions.assertNotNull(response2);

    }

    @Test
    void generateMultiHtmlCode() {

//        MultiHtmlCodeResponse response = aiService.generateMultiHtmlCode("做一个简单的任务记录网站，不超过50行代码");
//        Assertions.assertNotNull(response);
    }

    @Test
    void chatTest() {
        AiService aiService = factory.getAiService(1L, CodeGenerateType.HTML);
        String chat = aiService.chat("你好，我是Alex");
        System.out.println(chat);
        String chat1 = aiService.chat("你好，我是谁?");
        System.out.println(chat1);
    }
}