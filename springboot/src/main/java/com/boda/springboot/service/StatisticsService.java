package com.boda.springboot.service;

import com.boda.springboot.vo.HotCourseVO;
import com.boda.springboot.vo.HotResourceVO;
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

    /**
     * 获取热门课程
     * @param limit 限制条数
     */
    java.util.List<HotCourseVO> getHotCourses(int limit);

    /**
     * 获取热门资源
     * @param limit 限制条数
     */
    java.util.List<HotResourceVO> getHotResources(int limit);
}

