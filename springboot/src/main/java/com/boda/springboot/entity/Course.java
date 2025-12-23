package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体类
 * 存储课程基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID,主键
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 开课学院
     */
    private String college;

    /**
     * 课程封面图片URL
     */
    private String coverImage;

    /**
     * 课程状态: 0-关闭, 1-开放
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer isDeleted;

    /**
     * 邀请码（非数据库字段，仅在查询教师课程时使用）
     */
    private String inviteCode;
}
