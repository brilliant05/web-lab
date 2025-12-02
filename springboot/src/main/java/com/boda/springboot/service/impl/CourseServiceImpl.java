package com.boda.springboot.service.impl;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.entity.StudentCourse;
import com.boda.springboot.entity.TeacherCourse;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.CourseMapper;
import com.boda.springboot.mapper.StudentCourseMapper;
import com.boda.springboot.mapper.TeacherCourseMapper;
import com.boda.springboot.service.CourseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 课程服务实现类
 */
@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    /**
     * 保存课程信息
     * @param course 课程信息
     */
    @Override
    public void saveCourse(Course course) {
        Course existCourse = courseMapper.selectByCourseCode(course.getCourseCode());
        if (existCourse != null) {
            throw new ServiceException("400", "课程编号已存在");
        }
        courseMapper.save(course);
    }

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(CoursePageQueryDTO coursePageQueryDTO) {
        // 1. 开启分页（PageHelper 会自动拦截下一条 SQL 查询）
        PageHelper.startPage(coursePageQueryDTO.getPageNum(), coursePageQueryDTO.getPageSize());

        // 2. 执行查询（PageHelper 会自动分页）
        Page<Course> page = courseMapper.pageQuery(coursePageQueryDTO);

        // 3. 封装分页结果
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 根据课程ID查询课程信息
     * @param courseId 课程ID
     * @return 课程信息
     */
    @Override
    public Course getCourseById(Long courseId) {
        if (courseId == null) {
            throw new ServiceException("400", "课程ID不能为空");
        }

        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("404", "课程不存在");
        }

        return course;
    }

    /**
     * 动态更新课程信息
     * @param course 课程信息
     */
    @Override
    public void updateCourse(Course course) {
        // 1. 校验课程ID
        if (course.getCourseId() == null) {
            throw new ServiceException("400", "课程ID不能为空");
        }

        // 2. 检查课程是否存在
        Course existCourse = courseMapper.selectById(course.getCourseId());
        if (existCourse == null) {
            throw new ServiceException("404", "课程不存在");
        }

        // 3. 如果要更新课程编号，检查新编号是否已被其他课程使用
        if (course.getCourseCode() != null && !course.getCourseCode().equals(existCourse.getCourseCode())) {
            Course checkCourse = courseMapper.selectByCourseCode(course.getCourseCode());
            if (checkCourse != null && !checkCourse.getCourseId().equals(course.getCourseId())) {
                throw new ServiceException("400", "课程编号已被其他课程使用");
            }
        }

        // 4. 执行动态更新
        courseMapper.update(course);
    }

    /**
     * 删除课程（需处理关联数据）
     * @param courseId 课程ID
     */
    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        log.info("删除课程 - 课程ID: {}", courseId);

        // 1. 校验课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("404", "课程不存在");
        }

        // 2. 检查是否有学生已加入
        Integer studentCount = studentCourseMapper.countByCourseId(courseId);
        if (studentCount != null && studentCount > 0) {
            throw new ServiceException("400", "该课程已有 " + studentCount + " 名学生加入，无法删除");
        }

        // 3. 删除教师课程关联
        teacherCourseMapper.deleteByCourseId(courseId);

        // 4. 逻辑删除课程
        courseMapper.deleteById(courseId);

        log.info("课程删除成功 - 课程ID: {}", courseId);
    }

    /**
     * 为课程分配教师
     * @param courseId 课程ID
     * @param teacherId 教师ID
     */
    @Override
    @Transactional
    public void assignTeacher(Long courseId, Long teacherId) {
        log.info("为课程分配教师 - 课程ID: {}, 教师ID: {}", courseId, teacherId);

        // 1. 校验课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("404", "课程不存在");
        }

        // 2. 检查是否已经分配
        TeacherCourse existRelation = teacherCourseMapper.selectByTeacherAndCourse(teacherId, courseId);
        if (existRelation != null) {
            throw new ServiceException("400", "该教师已被分配到此课程");
        }

        // 3. 添加关联
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTeacherId(teacherId);
        teacherCourse.setCourseId(courseId);
        teacherCourseMapper.save(teacherCourse);

        log.info("教师分配成功");
    }

    /**
     * 移除课程教师
     * @param courseId 课程ID
     * @param teacherId 教师ID
     */
    @Override
    @Transactional
    public void removeTeacher(Long courseId, Long teacherId) {
        log.info("移除课程教师 - 课程ID: {}, 教师ID: {}", courseId, teacherId);

        // 1. 检查关联是否存在
        TeacherCourse relation = teacherCourseMapper.selectByTeacherAndCourse(teacherId, courseId);
        if (relation == null) {
            throw new ServiceException("404", "该教师未被分配到此课程");
        }

        // 2. 删除关联
        teacherCourseMapper.delete(teacherId, courseId);

        log.info("教师移除成功");
    }

    /**
     * 获取我的课程列表（教师）
     * @param teacherId 教师ID
     * @return 课程列表
     */
    @Override
    public PageResult getMyCourses(Long teacherId, Integer pageNum, Integer pageSize) {
        log.info("查询教师课程列表 - 教师ID: {}", teacherId);

        PageHelper.startPage(pageNum, pageSize);
        Page<Course> page = (Page<Course>) courseMapper.selectByTeacherId(teacherId);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 学生加入课程（通过邀请码）
     * @param studentId 学生ID
     * @param inviteCode 邀请码
     */
    @Override
    @Transactional
    public void joinCourse(Long studentId, String inviteCode) {
        log.info("学生加入课程 - 学生ID: {}, 邀请码: {}", studentId, inviteCode);

        // 1. 根据邀请码查询课程
        Course course = courseMapper.selectByInviteCode(inviteCode);
        if (course == null) {
            throw new ServiceException("404", "邀请码无效或课程不存在");
        }

        // 2. 检查课程状态
        if (course.getStatus() == 0) {
            throw new ServiceException("400", "该课程已关闭，无法加入");
        }

        // 3. 检查是否已加入
        StudentCourse existRelation = studentCourseMapper.selectByStudentAndCourse(studentId, course.getCourseId());
        if (existRelation != null) {
            throw new ServiceException("400", "您已加入该课程");
        }

        // 4. 添加关联
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(course.getCourseId());
        studentCourseMapper.save(studentCourse);

        log.info("学生加入课程成功 - 课程: {}", course.getCourseName());
    }
}
