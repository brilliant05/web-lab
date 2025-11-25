package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源收藏实体类
 * 存储学生收藏的资源
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 学生ID,逻辑外键关联user表
     */
    private Long studentId;

    /**
     * 资源ID,逻辑外键关联resource表
     */
    private Long resourceId;

    /**
     * 收藏时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
