package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.Result;
import com.boda.springboot.service.StatisticsService;
import com.boda.springboot.vo.StatisticsOverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器
 */
@RestController
@Slf4j
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取系统概览统计数据
     * 对应API：GET /api/v1/admin/statistics/overview
     */
    @GetMapping("/overview")
    @RequireRole(Constant.ROLE_ADMIN)
    public Result<StatisticsOverviewVO> getOverview() {
        log.info("获取系统概览统计数据");
        StatisticsOverviewVO overview = statisticsService.getOverview();
        return Result.success(overview);
    }
}

