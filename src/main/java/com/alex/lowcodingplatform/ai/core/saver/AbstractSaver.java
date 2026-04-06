package com.alex.lowcodingplatform.ai.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author wsh
 * @date 2026/4/5
 */
public abstract class AbstractSaver<T> {

    private static final String OUTPUT_DIR_PREFIX = "/tmp/code_output";


    /**
     * 保存文件的模版方法
     * @param result
     * @return
     */
    public final File save(T result) {
        // 1. 校验
        validate(result);
        // 2. 创建目录
        String dirPath = buildDirPath();
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
     * 构建唯一路径： /tmp/output/{type}_雪花ID
     * @return
     */
    private String buildDirPath() {
        String dir = StrUtil.format("{}_{}", this.generateType().getValue(), IdUtil.getSnowflakeNextId());
        String dirPath = OUTPUT_DIR_PREFIX + File.separator + dir;
        // 创建目录
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

}
