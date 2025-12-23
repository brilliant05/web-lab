package com.boda.springboot.dto;


import lombok.Data;

/**
 * 教师分页查询 DTO
 */
@Data
public class TeacherPageQueryDTO {
    /**
     * 页码
     */
    private int pageNum = 1;

    /**
     * 每页记录数
     */
    private int pageSize = 10;

    /**
     * 账号状态: 0-禁用, 1-正常
     */
    private Integer status;

    /**
     * 职称: 助教、讲师、副教授、教授
     */
    private String jobTitle;

    /**
     * 真实姓名（模糊查询）
     */
    private String realName;

    /**
     * 学院
     */
    private String college;
}
