package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资源更新DTO
 */
@Data
public class ResourceUpdateDTO implements Serializable {

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
     * 资源标签
     */
    private String tags;

    /**
     * 可见性
     */
    private String visibility;

    /**
     * 是否置顶
     */
    private Integer isTop;
}

