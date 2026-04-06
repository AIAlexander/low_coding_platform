package com.alex.lowcodingplatform.ai.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.constant.AppConstant;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author wsh
 * @date 2026/4/5
 */
public abstract class AbstractSaver<T> {

    private static final String OUTPUT_DIR_PREFIX = AppConstant.CODE_OUTPUT_ROOT_DIR;


    /**
     * 保存文件的模版方法
     * @param result
     * @param appId
     * @return
     */
    public final File save(T result, Long appId) {
        // 1. 校验
        validate(result);
        // 2. 创建目录
        String dirPath = buildDirPath(appId);
        // 3. 保存文件
        saveFile(result, dirPath);
        // 4. 返回文件
        return new File(dirPath);
    }

    /**
     * 定义生成的文件类型
     * @return
     */
    protected abstract CodeGenerateType generateType();

    /**
     * 保存文件
     * @param result
     */
    protected abstract void saveFile(T result, String baseDir);

    /**
     * 写入文件
     * @param dirPath
     * @param fileName
     * @param content
     */
    protected void writeFile(String dirPath, String fileName, String content) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, new File(filePath), StandardCharsets.UTF_8);
    }

    protected void validate(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.PARAMS_ERROR);
    }


    /**
     * 构建唯一路径： /tmp/code_output/{type}_{appId}
     * @param appId
     * @return
     */
    private String buildDirPath(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR, "App ID不能为空");
        String dir = StrUtil.format("{}_{}", generateType().getValue(), appId);
        String dirPath = OUTPUT_DIR_PREFIX + File.separator + dir;
        // 创建目录
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

}
