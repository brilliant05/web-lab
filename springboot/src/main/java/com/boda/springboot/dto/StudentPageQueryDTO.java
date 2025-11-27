package com.boda.springboot.dto;

import lombok.Data;

@Data
public class StudentPageQueryDTO {
    /**
     * 页码
     */
    private int pageNum = 1;

    /**
     * 每页记录数
     */
    private int pageSize = 10;

    /**
     * 账号状态: 0-禁用, 1-正常   对应user表的status字段
     */
    private Integer status;

    /**
     * 学院名称   对应user表的college字段
     */
    private String college;

    /**
     * 关键词   搜索姓名/学号，对应user表的real_name/student_id字段
     */
    private String keyword;
}