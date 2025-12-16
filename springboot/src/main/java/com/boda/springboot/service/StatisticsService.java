package com.boda.springboot.service;

import com.boda.springboot.vo.StatisticsOverviewVO;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    /**
     * 获取系统概览统计数据
     * @return 统计数据
     */
    StatisticsOverviewVO getOverview();
}

