package com.alex.lowcodingplatform.service;

import com.alex.lowcodingplatform.model.dto.app.AppAddRequest;
import com.alex.lowcodingplatform.model.dto.app.AppDeployRequest;
import com.alex.lowcodingplatform.model.dto.app.AppQueryRequest;
import com.alex.lowcodingplatform.model.entity.App;
import com.alex.lowcodingplatform.model.entity.User;
import com.alex.lowcodingplatform.model.vo.app.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author wsh
 * @date 2026/4/6
 */
public interface AppService extends IService<App> {

    Flux<String> chatToGenCode(Long appId, String userMessage, User loginUser);

    Long addApp(AppAddRequest appAddRequest, User loginUser);

    String deployApp(Long appId, User loginUser);

    void generateAppScreenshotAsync(Long appId, String appUrl);

    AppVO getAppVO(App app);

    List<AppVO> getAppVOList(List<App> appList);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

}
