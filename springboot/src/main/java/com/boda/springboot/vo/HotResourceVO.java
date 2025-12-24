package com.boda.springboot.vo;

import lombok.Data;

/**
 * 热门资源展示 VO
 */
@Data
public class HotResourceVO {
    private Long resourceId;
    private String resourceTitle;
    private String fileType;
    private Long downloadCount;
    private Long viewCount;
    private String courseName;
}

