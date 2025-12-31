package com.boda.springboot.vo;

import com.boda.springboot.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 课程视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CourseVO extends Course {
    /**
     * 选课学生人数
     */
    private Integer studentCount;
}
