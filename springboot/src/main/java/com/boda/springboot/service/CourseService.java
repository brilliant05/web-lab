package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.entity.User;
import com.boda.springboot.vo.CourseVO;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {

    /**
     * 保存课程信息
     * @param course 课程信息
     */
    void saveCourse(Course course);

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件
     * @return 分页结果
     */
    PageResult pageQuery(CoursePageQueryDTO coursePageQueryDTO);

    /**
     * 根据课程ID获取课程信息
     * @param courseId 课程ID
     * @return 课程信息
     */
    Course getCourseById(Long courseId);

    /**
     * 更新课程信息
     * @param course 课程信息
     */
    void updateCourse(Course course);

    /**
     * 删除课程（需处理关联数据）
     * @param courseId 课程ID
     */
    void deleteCourse(Long courseId);

    /**
     * 为课程分配教师
     * @param courseId 课程ID
     * @param teacherId 教师ID
     */
    void assignTeacher(Long courseId, Long teacherId);

    /**
     * 移除课程教师
     * @param courseId 课程ID
     * @param teacherId 教师ID
     */
    void removeTeacher(Long courseId, Long teacherId);

    /**
     * 获取我的课程列表（教师）
     * @param teacherId 教师ID
     * @return 课程列表
     */
    PageResult getMyCourses(Long teacherId, Integer pageNum, Integer pageSize);

    /**
     * 更新课程邀请码
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @param inviteCode 邀请码
     */
    void updateInviteCode(Long teacherId, Long courseId, String inviteCode);

    /**
     * 填充课程邀请码（如果是该课程的教师）
     * @param course 课程对象
     * @param teacherId 教师ID
     */
    void enrichCourseWithInviteCode(Course course, Long teacherId);

    /**
     * 学生加入课程（通过邀请码）
     * @param studentId 学生ID
     * @param inviteCode 邀请码
     */
    void joinCourse(Long studentId, String inviteCode);

    /**
     * 获取课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<User> getCourseStudents(Long courseId);

    /**
     * 获取课程的教师列表
     * @param courseId 课程ID
     * @return 教师列表
     */
    List<User> getCourseTeachers(Long courseId);

    /**
     * 获取教师的课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<CourseVO> getTeacherCourses(Long teacherId);

    /**
     * 获取学生的课程列表
     * @param studentId 学生ID
     * @return 课程列表
     */
    List<Course> getStudentCourses(Long studentId);
}
