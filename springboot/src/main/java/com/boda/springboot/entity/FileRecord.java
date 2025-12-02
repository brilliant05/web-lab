package com.boda.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件上传记录实体类
 * 记录所有通过七牛云上传的文件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件记录ID,主键
     */
    private Long fileId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 七牛云文件URL
     */
    private String fileUrl;

    /**
     * 七牛云文件Key（用于删除）
     */
    private String fileKey;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    private String fileType;

    /**
     * 上传用户ID
     */
    private Long uploaderId;

    /**
     * 上传用户名
     */
    private String uploaderName;

    /**
     * 关联类型: course_cover-课程封面, user_avatar-用户头像, resource-学习资源
     */
    private String relatedType;

    /**
     * 关联ID（如课程ID、用户ID等）
     */
    private Long relatedId;

    /**
     * 文件用途描述
     */
    private String purpose;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 逻辑删除: 0-未删除, 1-已删除
     */
    private Integer isDeleted;
}

