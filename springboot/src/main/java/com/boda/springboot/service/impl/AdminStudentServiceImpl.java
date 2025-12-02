package com.boda.springboot.service.impl;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.AssignCourseDTO;
import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.entity.StudentCourse;
import com.boda.springboot.entity.TeacherCourse;
import com.boda.springboot.entity.User;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.AdminStudentMapper;
import com.boda.springboot.mapper.CourseMapper;
import com.boda.springboot.mapper.StudentCourseMapper;
import com.boda.springboot.mapper.TeacherCourseMapper;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.AdminStudentService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AdminStudentServiceImpl implements AdminStudentService {

    @Autowired
    private AdminStudentMapper adminStudentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder; // 注入密码加密器
    @Autowired
    private StudentCourseMapper studentCourseMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

    /**
     *保存学生信息
     */
    public void saveStudent(User student) {
        // 校验用户名是否重复
        if (userMapper.selectByUsername(student.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置学生角色+默认密码
        student.setRole(Constant.ROLE_STUDENT);
        student.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userMapper.save(student); // 复用UserMapper的save方法
    }

    /**
     * 分页查询学生
     */
    @Override
    public PageResult pageQuery(StudentPageQueryDTO studentPageQueryDTO) {
        log.info("分页查询学生，条件：{}", studentPageQueryDTO);
        PageHelper.startPage(studentPageQueryDTO.getPageNum(), studentPageQueryDTO.getPageSize());
        Page<User> page = adminStudentMapper.pageQuery(studentPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 获取学生详情
     */
    @Override
    public User getById(Long studentId) {
        log.info("查询学生详情，ID：{}", studentId);
        // 先校验ID是否为空
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        User student = adminStudentMapper.getById(studentId);
        if (student != null && !Constant.ROLE_STUDENT.equals(student.getRole())) {
            throw new IllegalArgumentException("该用户不是学生");
        }
        return student;
    }

    /**
     * 更新学生信息
     */
    @Override
    public void updateStudent(User student) {
        log.info("更新学生信息：{}", student);
        if (student.getUserId() == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        // 密码加密
        if (student.getPassword() != null && !student.getPassword().trim().isEmpty()) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }
        // 确保角色为学生
        student.setRole(Constant.ROLE_STUDENT);
        // 调用Mapper更新
        adminStudentMapper.updateStudent(student);
    }

    /**
     * 管理员为学生分配课程
     */
    @Override
    @Transactional
    public void assignCourse(Long studentId, AssignCourseDTO assignCourseDTO) {
        log.info("管理员为学生分配课程 - 学生ID: {}, 课程ID: {}, 教师ID: {}",
                studentId, assignCourseDTO.getCourseId(), assignCourseDTO.getTeacherId());

        // 1. 校验学生是否存在且为学生角色
        User student = adminStudentMapper.getById(studentId);
        if (student == null) {
            throw new ServiceException("404", "学生不存在");
        }
        if (!Constant.ROLE_STUDENT.equals(student.getRole())) {
            throw new ServiceException("400", "该用户不是学生");
        }

        // 2. 校验课程是否存在
        Course course = courseMapper.selectById(assignCourseDTO.getCourseId());
        if (course == null) {
            throw new ServiceException("404", "课程不存在");
        }

        // 3. 校验教师是否已分配到该课程
        TeacherCourse teacherCourse = teacherCourseMapper.selectByTeacherAndCourse(
                assignCourseDTO.getTeacherId(), assignCourseDTO.getCourseId());
        if (teacherCourse == null) {
            throw new ServiceException("400", "该教师未被分配到此课程，请先为课程分配教师");
        }

        // 4. 检查学生是否已加入该课程
        StudentCourse existRelation = studentCourseMapper.selectByStudentAndCourse(
                studentId, assignCourseDTO.getCourseId());
        if (existRelation != null) {
            throw new ServiceException("400", "该学生已加入此课程");
        }

        // 5. 创建学生课程关联
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(assignCourseDTO.getCourseId());
        studentCourse.setTeacherId(assignCourseDTO.getTeacherId());
        studentCourse.setJoinMethod("ADMIN_ASSIGN"); // 管理员分配
        studentCourseMapper.save(studentCourse);

        log.info("学生课程分配成功 - 学生: {}, 课程: {}, 教师: {}",
                student.getRealName(), course.getCourseName(), assignCourseDTO.getTeacherId());
    }

    /**
     * 管理员移除学生课程
     */
    @Override
    @Transactional
    public void removeCourse(Long studentId, Long courseId) {
        log.info("管理员移除学生课程 - 学生ID: {}, 课程ID: {}", studentId, courseId);

        // 1. 检查关联是否存在
        StudentCourse relation = studentCourseMapper.selectByStudentAndCourse(studentId, courseId);
        if (relation == null) {
            throw new ServiceException("404", "该学生未加入此课程");
        }

        // 2. 删除关联
        studentCourseMapper.delete(studentId, courseId);

        log.info("学生课程移除成功");
    }
}