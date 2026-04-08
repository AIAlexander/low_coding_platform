package com.alex.lowcodingplatform.service.impl;

import com.alex.lowcodingplatform.service.ScreenshotService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wangshuhao
 * @date 2026/4/8
 */
@SpringBootTest
class ScreenshotServiceImplTest {

    @Resource
    private ScreenshotService screenshotService;

    @Test
    void generateAndUploadScreenshot() {
        String s = screenshotService.generateAndUploadScreenshot("https://www.baidu.com");
        Assertions.assertNotNull(s);
        System.out.println(s);
    }
}