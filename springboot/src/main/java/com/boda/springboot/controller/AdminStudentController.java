package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.common.Result;
import com.boda.springboot.dto.StudentPageQueryDTO;
import com.boda.springboot.entity.User;
import com.boda.springboot.service.AdminStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/students") // 对应API基础路径：/api/v1/admin/students
public class AdminStudentController {

    @Autowired
    private AdminStudentService adminStudentService;

    /**
     * 8.2.1 分页获取学生列表
     * 对应API：GET /api/v1/admin/students?pageNum=1&pageSize=10&keyword=张&college=计算机学院
     */
    @GetMapping("/page")
    @RequireRole("ADMIN") // 仅管理员可访问
    public Result<PageResult> pageQueryStudents(StudentPageQueryDTO studentPageQueryDTO) {
        log.info("分页查询学生列表，查询条件：{}", studentPageQueryDTO);
        PageResult pageResult = adminStudentService.pageQuery(studentPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 8.2.2 获取学生详情
     * 对应API：GET /api/v1/admin/students/{studentId}
     */
    @GetMapping("/{studentId}")
    @RequireRole("ADMIN")
    public Result<User> getStudentDetail(@PathVariable Long studentId) {
        log.info("获取学生详情，学生ID：{}", studentId);
        User student = adminStudentService.getById(studentId);
        return Result.success(student);
    }

    /**
     * 8.2.3 启用/禁用学生账号
     * 对应API：PUT /api/v1/admin/students/{studentId}/status
     */
    @PutMapping("/{studentId}/status")
    @RequireRole("ADMIN")
    public Result enableAndDisableStudent(
            @PathVariable Long studentId,
            @RequestBody User student
    ) {
        log.info("启用/禁用学生账号，学生ID：{}，状态参数：{}", studentId, student);
        // 设置学生ID确保更新的是指定学生
        student.setUserId(studentId);
        adminStudentService.updateStudent(student);
        return Result.success("学生账号状态修改成功！");
    }

    /**
     * 8.2.4 删除学生
     * 对应API：DELETE /api/v1/admin/students/{studentId}
     */
    @DeleteMapping("/{studentId}")
    @RequireRole("ADMIN")
    public Result deleteStudent(@PathVariable Long studentId) {
        log.info("删除学生，学生ID：{}", studentId);
        // 构造学生对象，设置逻辑删除标识
        User student = new User();
        student.setUserId(studentId);
        student.setStatus(0); // 状态设为禁用
        student.setIsDeleted(Constant.DELETE_IS); // 标记为已删除
        adminStudentService.updateStudent(student);
        return Result.success("学生删除成功！");
    }

    /**
     * 更新学生信息
     * 对应API：PUT /api/v1/admin/students/{studentId}
     */
    @PutMapping("/{studentId}")
    @RequireRole("ADMIN")
    public Result updateStudent(
            @PathVariable Long studentId,
            @RequestBody User student
    ) {
        log.info("更新学生信息，学生ID：{}，信息：{}", studentId, student);
        student.setUserId(studentId); // 确保更新的是指定学生
        adminStudentService.updateStudent(student);
        return Result.success("学生信息更新成功！");
    }
}
