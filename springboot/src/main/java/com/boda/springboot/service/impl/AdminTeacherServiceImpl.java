package com.boda.springboot.service.impl;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.TeacherPageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.entity.TeacherCourse;
import com.boda.springboot.entity.User;
import com.boda.springboot.mapper.AdminTeacherMapper;
import com.boda.springboot.mapper.CourseMapper;
import com.boda.springboot.mapper.TeacherCourseMapper;
import com.boda.springboot.mapper.UserMapper;
import com.boda.springboot.service.AdminTeacherService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@Slf4j
public class AdminTeacherServiceImpl implements AdminTeacherService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AdminTeacherMapper adminTeacherMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    private TeacherCourseMapper teacherCourseMapper;
    @Autowired
    private CourseMapper courseMapper;
    
    /**
     * 保存教师信息（包含课程分配）
     *
     * @param teacher 教师信息
     * @param courseIds 要分配的课程ID列表
     */
    @Transactional
    public void saveTeacher(User teacher, List<Long> courseIds) {
        if(userMapper.selectByUsername(teacher.getUsername()) != null){
            throw new RuntimeException("用户名已存在");
        }
        if(teacher.getEmail() == null || teacher.getEmail().trim().isEmpty()){
            teacher.setEmail(null);
        }
        if(teacher.getPhone() == null || teacher.getPhone().trim().isEmpty()){
            teacher.setPhone(null);
        }
        teacher.setRole(Constant.ROLE_TEACHER);
        teacher.setPassword(passwordEncoder.encode(Constant.DEFAULT_PASSWORD));
        userMapper.save(teacher);
        
        // 分配课程
        if (courseIds != null && !courseIds.isEmpty()) {
            Long teacherId = teacher.getUserId();
            for (Long courseId : courseIds) {
                // 检查课程是否存在
                Course course = courseMapper.selectById(courseId);
                if (course == null) {
                    log.warn("课程不存在，跳过分配 - 课程ID: {}", courseId);
                    continue;
                }
                
                // 检查是否已存在关联
                TeacherCourse existRelation = teacherCourseMapper.selectByTeacherAndCourse(teacherId, courseId);
                if (existRelation != null) {
                    log.warn("教师已被分配到此课程，跳过 - 教师ID: {}, 课程ID: {}", teacherId, courseId);
                    continue;
                }
                
                // 创建教师课程关联
                TeacherCourse teacherCourse = new TeacherCourse();
                teacherCourse.setTeacherId(teacherId);
                teacherCourse.setCourseId(courseId);
                teacherCourseMapper.save(teacherCourse);
                
                log.info("教师课程分配成功 - 教师ID: {}, 课程ID: {}", teacherId, courseId);
            }
        }
    }

    /**
     * 更新教师信息
     *
     * @param teacher 教师信息
     */
    @Override
    public void updateTeacher(User teacher) {
        if (teacher.getUserId() == null) {
            throw new RuntimeException("添加失败");
        }

        // 调用 Mapper 更新
        adminTeacherMapper.updateTeacher(teacher);
    }

    /**
     * 分页查询教师
     *
     * @param teacherPageQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(TeacherPageQueryDTO teacherPageQueryDTO) {
        // 1. 开启分页（PageHelper 会自动拦截下一条 SQL 查询）
        PageHelper.startPage(teacherPageQueryDTO.getPageNum(), teacherPageQueryDTO.getPageSize());

        // 2. 执行查询（PageHelper 会自动分页）
        Page<User> page = adminTeacherMapper.pageQuery(teacherPageQueryDTO);

        // 3. 封装分页结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 重置教师密码
     */
    @Override
    public void resetPassword(Long teacherId) {
        log.info("重置教师密码，ID：{}", teacherId);
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        User teacher = new User();
        teacher.setUserId(teacherId);
        teacher.setPassword(passwordEncoder.encode("123456"));
        adminTeacherMapper.updateTeacher(teacher);
    }
}
