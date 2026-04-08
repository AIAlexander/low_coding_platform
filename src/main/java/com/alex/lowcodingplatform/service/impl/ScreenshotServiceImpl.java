package com.alex.lowcodingplatform.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;
import com.alex.lowcodingplatform.service.FileService;
import com.alex.lowcodingplatform.service.ScreenshotService;
import com.alex.lowcodingplatform.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author wangshuhao
 * @date 2026/4/8
 *
 * web截图服务
 *
 */
@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {

    @Resource
    private FileService fileService;


    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        ThrowUtils.throwIf(webUrl == null, ErrorCode.PARAMS_ERROR, "网页URL不能为空");
        log.info("开始生成网站截图, URL:{}", webUrl);
        // 1. 生成本地截图
        String imagePath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(imagePath == null, ErrorCode.SYSTEM_ERROR, "本地截图生成失败");
        try {
            // 2. 上传aliyun oss
            return fileService.uploadLocalFile(imagePath, generateScreenShotKey());
        } finally {
            // 3. 清除本地图片
            cleanupLocalFile(imagePath);
        }
    }

    /**
     * 拼接OSS文件名称：
     *      /screenshots/日期_随机字符串.jpg
     * @return
     */
    private String generateScreenShotKey() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        return String.format("screenshots/%s_%s", datePrefix, fileName);
    }


    private void cleanupLocalFile(String localPath) {
        File localFile = new File(localPath);
        if (localFile.exists()) {
            File parentDir = localFile.getParentFile();
            FileUtil.del(parentDir);
            log.info("本地截图文件已清理：{}", localFile);
        }
    }
}
