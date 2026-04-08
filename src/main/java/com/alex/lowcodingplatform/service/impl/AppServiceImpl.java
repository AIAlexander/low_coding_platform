package com.alex.lowcodingplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.lowcodingplatform.ai.core.builder.VueProjectBuilder;
import com.alex.lowcodingplatform.ai.core.handler.StreamHandlerExecutor;
import com.alex.lowcodingplatform.ai.facade.GenerateCodeFacade;
import com.alex.lowcodingplatform.ai.model.enums.CodeGenerateType;
import com.alex.lowcodingplatform.constant.AppConstant;
import com.alex.lowcodingplatform.exception.BusinessException;
import com.alex.lowcodingplatform.exception.ErrorCode;
import com.alex.lowcodingplatform.exception.ThrowUtils;
import com.alex.lowcodingplatform.mapper.AppMapper;
import com.alex.lowcodingplatform.model.dto.app.AppAddRequest;
import com.alex.lowcodingplatform.model.dto.app.AppDeployRequest;
import com.alex.lowcodingplatform.model.dto.app.AppQueryRequest;
import com.alex.lowcodingplatform.model.entity.App;
import com.alex.lowcodingplatform.model.entity.User;
import com.alex.lowcodingplatform.model.enums.ChatHistoryMessageTypeEnum;
import com.alex.lowcodingplatform.model.vo.app.AppVO;
import com.alex.lowcodingplatform.model.vo.user.UserVO;
import com.alex.lowcodingplatform.service.AppService;
import com.alex.lowcodingplatform.service.ChatHistoryService;
import com.alex.lowcodingplatform.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wsh
 * @date 2026/4/6
 */

@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private GenerateCodeFacade generateCodeFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;

    @Resource
    private VueProjectBuilder vueProjectBuilder;


    @Override
    public Flux<String> chatToGenCode(Long appId, String userMessage, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "App ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询APP
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "App 不存在");
        // 3. 当前用户只能生成自己的APP
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限生成代码");
        String typeStr = app.getCodeGenType();
        CodeGenerateType type = CodeGenerateType.getEnumByValue(typeStr);
        ThrowUtils.throwIf(type == null, ErrorCode.SYSTEM_ERROR, "代码生成类型不存在");
        // 4. 添加对话历史记录
        chatHistoryService.addChatMessage(appId, userMessage, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
        // 5. 调用ai生成代码
        Flux<String> stream = generateCodeFacade.generateCodeAndSaveStream(userMessage, type, appId);
        /**
         * 6. 下游需要根据上游返回的Stream，做特殊处理
         * VUE_PROJECT模式下，一个chunk是一个Json对象
         * 其他模式下，一个chunk是一个文本块
         */
        return streamHandlerExecutor.doExecute(stream, chatHistoryService, appId, loginUser, type);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "App ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");

        // 2. 获取APP信息
        App app = getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "App 不存在");
        // 用户只能部署自己的应用
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "无权限部署应用");

        // 3. 查看是否有deployKey
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            // 没有key就生成一个
            deployKey = RandomUtil.randomString(6);
        }
        // 获取app的信息
        String codeGenType = app.getCodeGenType();
        // app的名字
        String appPath = StrUtil.format("{}_{}", codeGenType, app.getId());
        String appDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + appPath;
        // 查看路径下是否有文件
        File sourceFile = new File(appDirPath);
        if (!sourceFile.exists() || !sourceFile.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "App不存在，请先生成App");
        }

        // 4. Vue项目特殊处理，部署时使用最新的代码进行构建
        if (StrUtil.equals(CodeGenerateType.VUE_PROJECT.getValue(), codeGenType)) {
            // Vue项目构建，直接打包
            boolean buildResult = vueProjectBuilder.buildProject(appDirPath);
            ThrowUtils.throwIf(!buildResult, ErrorCode.SYSTEM_ERROR, "Vue项目构建失败");
            // 检查dist文件是否存在
            File distDir = new File(appDirPath + File.separator + "dist");
            ThrowUtils.throwIf(!distDir.exists() || !distDir.isDirectory(),
                    ErrorCode.SYSTEM_ERROR, "Vue项目构建失败，dist目录不存在");
            // 只需要拷贝dist文件到 output_deploy下即可
            sourceFile = distDir;
            log.info("Vue项目构建成功，部署dist目录：{}", distDir.getAbsolutePath());
        }
        // 5. 复制文件到部署目录
        String deployDir = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceFile, new File(deployDir), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败");
        }
        // 6. 部署成功，更新数据库
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean result = this.updateById(updateApp);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "部署失败");
        return StrUtil.format("{}/{}", AppConstant.CODE_DEPLOY_HOST, deployKey);
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }


    /**
     * 封装查询接口
     * @param appQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
//        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
//                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));

        if (codeGenType != null) {
            queryWrapper.eq("codeGenType", codeGenType);
        }
        if (deployKey != null) {
            queryWrapper.eq("deployKey", deployKey);
        }
        return queryWrapper;
    }

    /**
     * 删除应用时关联删除对话历史
     *
     * @param id 应用ID
     * @return 是否成功
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        // 转换为 Long 类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先删除关联的对话历史
        try {
            chatHistoryService.clearChatHistory(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }

}
