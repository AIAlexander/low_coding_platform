package com.alex.lowcodingplatform.service.impl;

import com.alex.lowcodingplatform.config.OssConfig;
import com.alex.lowcodingplatform.constant.AppConstant;
import com.alex.lowcodingplatform.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wangshuhao
 * @date 2026/4/8
 */
@SpringBootTest
class FileServiceImplTest {

    @Autowired
    private FileService fileService;

    @Autowired
    private OssConfig ossConfig;

    @Test
    void upload() {

        String localPath = AppConstant.SCREEN_SHOT_ROOT_DIR + File.separator + "a7518fbb/70658_compressed.jpg";
        String s = fileService.uploadLocalFile(localPath, "test3.jpg");
        System.out.println(s);
    }
}