package com.boda.springboot.service.impl;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.entity.StudentCourse;
import com.boda.springboot.entity.User;
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

import java.util.List;

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
     * 保存学生信息（包含课程分配）
     */
    @Override
    @Transactional
    public void saveStudent(User student, List<Long> courseIds) {
        // 校验用户名是否重复
        if (userMapper.selectByUsername(student.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置学生角色+默认密码
        student.setRole(Constant.ROLE_STUDENT);
        student.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userMapper.save(student); // 复用UserMapper的save方法
        
        // 分配课程
        if (courseIds != null && !courseIds.isEmpty()) {
            Long studentId = student.getUserId();
            for (Long courseId : courseIds) {
                // 检查课程是否存在
                Course course = courseMapper.selectById(courseId);
                if (course == null) {
                    log.warn("课程不存在，跳过分配 - 课程ID: {}", courseId);
                    continue;
                }
                
                // 检查是否已存在关联
                StudentCourse existRelation = studentCourseMapper.selectByStudentAndCourse(studentId, courseId);
                if (existRelation != null) {
                    log.warn("学生已加入该课程，跳过 - 学生ID: {}, 课程ID: {}", studentId, courseId);
                    continue;
                }
                
                // 获取课程的第一个教师ID（如果存在）
                List<Long> teacherIds = teacherCourseMapper.selectTeacherIdsByCourseId(courseId);
                Long teacherId = teacherIds != null && !teacherIds.isEmpty() ? teacherIds.get(0) : null;
                
                // 创建学生课程关联
                StudentCourse studentCourse = new StudentCourse();
                studentCourse.setStudentId(studentId);
                studentCourse.setCourseId(courseId);
                studentCourse.setTeacherId(teacherId);
                studentCourse.setJoinMethod("ADMIN_ASSIGN"); // 管理员分配
                studentCourseMapper.save(studentCourse);
                
                log.info("学生课程分配成功 - 学生ID: {}, 课程ID: {}, 教师ID: {}", studentId, courseId, teacherId);
            }
        }
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

}