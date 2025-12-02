package com.boda.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员为学生分配课程 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignCourseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 授课教师ID
     */
    private Long teacherId;
}

