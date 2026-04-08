package com.alex.lowcodingplatform.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wangshuhao
 * @date 2026/4/8
 */
class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String s = WebScreenshotUtils.saveWebPageScreenshot("https://www.baidu.com");
        Assertions.assertNotNull(s);
    }
}