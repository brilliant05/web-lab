package com.boda.springboot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统概览统计数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsOverviewVO {
    /**
     * 用户总数
     */
    private Long totalUsers;

    /**
     * 学生总数
     */
    private Long totalStudents;

    /**
     * 教师总数
     */
    private Long totalTeachers;

    /**
     * 课程总数
     */
    private Long totalCourses;

    /**
     * 资源总数
     */
    private Long totalResources;

    /**
     * 问题总数
     */
    private Long totalQuestions;

    /**
     * 回答总数
     */
    private Long totalAnswers;
}

