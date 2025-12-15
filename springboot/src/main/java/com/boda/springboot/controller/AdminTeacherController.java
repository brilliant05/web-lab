package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.common.Result;
import com.boda.springboot.dto.TeacherPageQueryDTO;
import com.boda.springboot.entity.User;
import com.boda.springboot.service.AdminTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin/teachers")
public class AdminTeacherController {
    @Autowired
    AdminTeacherService adminTeacherService;

    /**
     * 添加教师
     * @param teacher 教师信息
     * @return 添加结果
     */
    @PostMapping
    @RequireRole("ADMIN")
    public Result saveTeacher(@RequestBody User teacher){
        log.info("添加教师: {}", teacher);
        adminTeacherService.saveTeacher(teacher);
        return Result.success("添加成功!");
    }

    /**
     * 更新教师信息
     * @param teacherId 教师ID（从路径获取）
     * @param teacher 教师信息（从请求体获取）
     * @return 更新结果
     */
    @PutMapping("/{teacherId}")
    @RequireRole("ADMIN")
    public Result updateTeacher(
        @PathVariable Long teacherId,      // 从路径获取教师ID
        @RequestBody User teacher           // 从请求体获取教师信息
    ){
        log.info("更新教师 ID: {}, 信息: {}", teacherId, teacher);

        // 设置教师ID（确保更新的是正确的教师）
        teacher.setUserId(teacherId);

        // 调用服务层更新
        adminTeacherService.updateTeacher(teacher);

        return Result.success("更新成功!");
    }
    /**
     * 启用或禁用教师账号
     * @param teacherId 教师ID（从路径获取）
     * @param teacher 教师信息（从请求体获取）
     * @return 操作结果
     */
    @PutMapping({"/{teacherId}/status"})
    @RequireRole("ADMIN")
    public Result enableAndDisableTeacher(@RequestBody User teacher,@PathVariable Long teacherId){
        log.info("修改教师状态 ID: {}, 信息: {}", teacherId, teacher);
        teacher.setUserId(teacherId);
        adminTeacherService.updateTeacher(teacher);
        return Result.success("修改成功!");
    }

    @DeleteMapping("/{teacherId}")
    @RequireRole("ADMIN")
    public Result deleteTeacher(@PathVariable Long teacherId){
        log.info("删除教师 ID: {}", teacherId);
        User teacher = new User();
        teacher.setUserId(teacherId);
        teacher.setStatus(0); // 设置状态为禁用
        teacher.setIsDeleted(Constant.DELETE_IS); // 设置为逻辑删除
        adminTeacherService.updateTeacher(teacher);
        return Result.success("删除成功!");
    }

    /**
     * 分页查询教师
     * @param teacherPageQueryDTO 查询条件（包含分页参数）
     * @return 分页结果
     */
    @GetMapping
    @RequireRole("ADMIN")
    public Result<PageResult> pageQueryTeachers(TeacherPageQueryDTO teacherPageQueryDTO){
        log.info("分页查询教师: {}", teacherPageQueryDTO);
        PageResult pageResult = adminTeacherService.pageQuery(teacherPageQueryDTO);
        return Result.success(pageResult);
    }
}
