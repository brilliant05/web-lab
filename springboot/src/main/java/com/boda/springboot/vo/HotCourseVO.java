package com.boda.springboot.vo;

import lombok.Data;

/**
 * 热门课程展示 VO
 */
@Data
public class HotCourseVO {
    private Long courseId;
    private String courseName;
    private String college;
    private String coverImage;
    private Long studentCount;
}

