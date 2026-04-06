package com.alex.lowcodingplatform.service;

import com.alex.lowcodingplatform.model.dto.app.AppAddRequest;
import com.alex.lowcodingplatform.model.dto.app.AppQueryRequest;
import com.alex.lowcodingplatform.model.entity.App;
import com.alex.lowcodingplatform.model.entity.User;
import com.alex.lowcodingplatform.model.vo.app.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * @author wsh
 * @date 2026/4/6
 */
public interface AppService extends IService<App> {


    public AppVO getAppVO(App app);

    List<AppVO> getAppVOList(List<App> appList);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

}
