package com.boda.springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师课程关联实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    private Long id;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 分配时间
     */
    private LocalDateTime assignTime;
}

