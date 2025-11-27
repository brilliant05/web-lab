package com.boda.springboot.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程分页查询 DTO
 */
@Data
public class CoursePageQueryDTO {
    /**
     * 页码（默认第1页）
     */
    private int pageNum = 1;

    /**
     * 每页记录数（默认10条）
     */
    private int pageSize = 10;

    /**
     * 课程状态: 0-关闭, 1-开放
     */
    private Integer status;

    /**
     * 课程名称（模糊查询）
     */
    private String courseName;

    /**
     * 课程代码（模糊查询）
     */
    private String courseCode;

    /**
     * 学院名称（模糊查询）
     */
    private String college;

    /**
     * 课程创建时间
     */
    private LocalDateTime createTime;

}
