package com.alex.lowcodingplatform.ai.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.alex.lowcodingplatform.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author wangshuhao
 * @date 2026/4/7
 */

@Slf4j
public class FileWriteTool extends BaseTool{

    /**
     * 每个APP ID 对应一个项目，因此使用 appId 进行路径保存
     * @param relativeFilePath
     * @param content
     * @return
     */
    @Tool("写入文件到指定目录, 返回文件的相对路径")
    public String writeFile(@P("文件的相对路径") String relativeFilePath,
                            @P("写入文件的内容") String content,
                            @ToolMemoryId Long appId) {
        try {
            Path path = Paths.get(relativeFilePath);
            if (!path.isAbsolute()) {
                // 相对路径处理，创建基于appId的项目目录
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR, projectDirName);
                path = projectRoot.resolve(relativeFilePath);
            }
            // 创建父目录（如果不存在）
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            // 写入内容
            Files.write(path, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            log.info("文件已经成功写入:{}", path.toAbsolutePath());
            // 返回相对路径
            return "文件成功写入：" + relativeFilePath;
        } catch (Exception e) {
            String errorMsg = "文件写入失败：" + relativeFilePath + ", 错误：" + e.getMessage();
            log.error("写入文件时出错：{}", e.getMessage());
            return errorMsg;
        }
    }

    @Override
    public String getToolName() {
        return "writeFile";
    }

    @Override
    public String getDisplayName() {
        return "写入工具";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        String suffix = FileUtil.getSuffix(relativeFilePath);
        String content = arguments.getStr("content");
        return String.format("""
                        [工具调用] 写入文件 %s
                        ```%s
                        %s
                        ```
                        """, relativeFilePath, suffix, content);
    }
}
