package com.alex.lowcodingplatform.service;

import jakarta.servlet.http.HttpServletResponse;

/**
 * @author wangshuhao
 * @date 2026/4/8
 */
public interface ProjectDownloadService {


    /**
     * 下载压缩包
     * @param projectPath               项目路径
     * @param downloadName              下载包名称
     * @return
     */
    String downloadAsZip(String projectPath, String downloadName, HttpServletResponse response);

}
