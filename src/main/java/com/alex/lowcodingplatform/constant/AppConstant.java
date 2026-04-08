package com.alex.lowcodingplatform.constant;

import java.io.File;

/**
 * @author wsh
 * @date 2026/4/6
 */
public interface AppConstant {

    Integer GOOD_ADD_PRIORITY = 99;

    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost";

    String VUE_PROJECT_DIR_PREFIX = CODE_OUTPUT_ROOT_DIR + File.separator + "vue_project_";

    String SCREEN_SHOT_ROOT_DIR = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "screenshot";

}
