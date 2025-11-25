package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习资源实体类
 * 存储学习资料信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源ID,主键
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
     * 所属课程ID,逻辑外键关联course表
     */
    private Long courseId;

    /**
     * 上传者ID,逻辑外键关联user表
     */
    private Long uploaderId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文件类型: PDF, WORD, EXCEL, PPT, IMAGE, VIDEO, ZIP等
     */
    private String fileType;

    /**
     * 可见性: PUBLIC-全部学生, COURSE_ONLY-仅本课程学生
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
     * 是否置顶: 0-否, 1-是(仅教师可设置)
     */
    private Integer isTop;

    /**
     * 资源标签,逗号分隔
     */
    private String tags;

    /**
     * 状态: 0-已下架, 1-正常
     */
    private Integer status;

    /**
     * 上传时间
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
}
