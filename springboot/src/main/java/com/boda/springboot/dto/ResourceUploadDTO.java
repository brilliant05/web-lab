package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资源上传DTO
 */
@Data
public class ResourceUploadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源标题
     */
    private String resourceTitle;

    /**
     * 资源描述
     */
    private String description;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 可见性: PUBLIC-全部学生, COURSE_ONLY-仅本课程学生
     */
    private String visibility = "PUBLIC";

    /**
     * 资源标签，逗号分隔
     */
    private String tags;
}

