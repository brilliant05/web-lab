package com.boda.springboot.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资源分页查询DTO
 */
@Data
public class ResourcePageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 课程ID（筛选条件）
     */
    private Long courseId;

    /**
     * 可见性（筛选条件）
     */
    private String visibility;

    /**
     * 关键词（搜索标题、描述）
     */
    private String keyword;

    /**
     * 文件类型（PDF、WORD等）
     */
    private String fileType;

    /**
     * 排序字段：createTime、downloadCount、viewCount
     */
    private String orderBy = "createTime";

    /**
     * 排序方向：asc、desc
     */
    private String orderDir = "desc";
}

