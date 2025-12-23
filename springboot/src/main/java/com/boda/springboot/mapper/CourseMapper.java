package com.boda.springboot.mapper;

import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 课程 Mapper 接口
 */
@Mapper
public interface CourseMapper {

    /**
     * 根据课程编号查询课程
     * @param courseCode 课程编号
     * @return 课程信息
     */
    @Select("SELECT * FROM course WHERE course_code = #{courseCode} AND is_deleted = 0")
    Course selectByCourseCode(String courseCode);

    /**
     * 保存课程信息
     * @param course 课程信息
     */
    void save(Course course);

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件
     * @return 分页结果
     */
    Page<Course> pageQuery(CoursePageQueryDTO coursePageQueryDTO);
    /**
     * 根据课程ID查询课程
     * @param courseId 课程ID
     * @return 课程信息
     */
    @Select("SELECT * FROM course WHERE course_id = #{courseId} AND is_deleted = 0")
    Course selectById(Long courseId);

    /**
     * 动态更新课程信息
     * @param course 课程信息
     */
    void update(Course course);

    /**
     * 根据邀请码查询课程
     * @param inviteCode 邀请码
     * @return 课程信息
     */
    @Select("SELECT * FROM  teacher_course WHERE invite_code = #{inviteCode} ")
    Course selectByInviteCode(String inviteCode);

    /**
     * 查询教师的课程列表
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> selectByTeacherId(Long teacherId);

    /**
     * 删除课程（逻辑删除）
     * @param courseId 课程ID
     */
    void deleteById(Long courseId);

    /**
     * 统计课程总数
     * @return 课程总数
     */
    @Select("SELECT COUNT(*) FROM course WHERE is_deleted = 0")
    Integer countCourses();

    /**
     * 查询课程的学生列表
     * @param courseId 课程ID
     * @return 学生列表
     */
    List<User> selectStudentsByCourseId(Long courseId);

    /**
     * 查询课程的教师列表
     * @param courseId 课程ID
     * @return 教师列表
     */
    List<User> selectTeachersByCourseId(Long courseId);
}
