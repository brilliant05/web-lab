package com.boda.springboot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源列表VO
 * 用于资源列表展示
 */
@Data
public class ResourceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源ID
     */
    private Long resourceId;

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
     * 课程名称
     */
    private String courseName;

    /**
     * 上传者ID
     */
    private Long uploaderId;

    /**
     * 上传者姓名
     */
    private String uploaderName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件路径（URL）
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 可见性
     */
    private String visibility;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 资源标签
     */
    private String tags;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 是否已收藏（当前用户）
     */
    private Boolean isCollected;
}

