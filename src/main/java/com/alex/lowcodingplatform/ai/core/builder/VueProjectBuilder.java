package com.alex.lowcodingplatform.ai.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import dev.langchain4j.agent.tool.P;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author wangshuhao
 * @date 2026/4/8
 *
 * Vue项目构建
 *
 * 使用代码执行 npm 相关命令 进行构建 dist
 * 1. npm install
 * 2. npm run build
 *
 */
@Slf4j
@Component
public class VueProjectBuilder {


    public void buildProjectAsync(String path) {
        // 使用虚拟线程
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis()).start(() -> {
            try {
                buildProject(path);
            } catch (Exception e) {
                // 异常在线程里面处理
                log.error("异步构建vue项目失败，异常：{}", e.getMessage(), e);
            }
        });
    }


    public boolean buildProject(String path) {
        File projectDir = new File(path);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            log.error("项目目录不存在, {}", path);
            return false;
        }
        // 检查 package.json 是否存在
        File packageJson = new File(projectDir, "package.json");
        if (!packageJson.exists()) {
            log.error("package.json 不存在, {}", path);
            return false;
        }
        log.info("开始构建Vue项目...");
        // 执行install
        if (!executeNpmInstall(projectDir)) {
            log.error("npm install 失败");
            return false;
        }
        // 执行 npm run build
        if (!executeNpmBuild(projectDir)) {
            log.error("npm run build 失败");
            return false;
        }
        // 验证build是否成功
        File dist = new File(projectDir, "dist");
        if (!dist.exists()) {
            log.error("build 失败，dist 目录不存在");
            return false;
        }
        log.info("Vue项目构建成功, 地址:{}", projectDir);
        return true;
    }



    /**
     * 执行 npm install 命令
     */
    private boolean executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        String command = String.format("%s install", buildCommand("npm"));
        // 5分钟超时
        return executeCommand(projectDir, command, 300);
    }

    /**
     * 执行 npm run build 命令
     */
    private boolean executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        String command = String.format("%s run build", buildCommand("npm"));
        // 3分钟超时
        return executeCommand(projectDir, command, 180);
    }

    /**
     * 不同操作系统指令不同
     * @param baseCommand
     * @return
     */
    private String buildCommand(String baseCommand) {
        if (isWindows()) {
            return baseCommand + ".cmd";
        }
        return baseCommand;
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }



    /**
     * 执行命令
     *
     * @param workingDir     工作目录
     * @param command        命令字符串
     * @param timeoutSeconds 超时时间（秒）
     * @return 是否执行成功
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        try {
            log.info("在目录 {} 中执行命令: {}", workingDir.getAbsolutePath(), command);
            Process process = RuntimeUtil.exec(
                    null,
                    workingDir,
                    command.split("\\s+")
            );
            // 等待进程完成，设置超时
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                log.error("命令执行超时（{}秒），强制终止进程", timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                log.info("命令执行成功: {}", command);
                return true;
            } else {
                log.error("命令执行失败，退出码: {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("执行命令失败: {}, 错误信息: {}", command, e.getMessage());
            return false;
        }
    }

}
