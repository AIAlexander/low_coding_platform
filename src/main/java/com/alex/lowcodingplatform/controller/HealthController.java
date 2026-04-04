package com.alex.lowcodingplatform.controller;

import com.alex.lowcodingplatform.common.BaseResponse;
import com.alex.lowcodingplatform.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wsh
 * @date 2026/4/4
 */
@RestController
@RequestMapping("/health")
public class HealthController {


    @GetMapping
    public BaseResponse<String> health() {
        return ResultUtils.success("OK");
    }

}
