package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.common.Result;
import com.boda.springboot.dto.CoursePageQueryDTO;
import com.boda.springboot.entity.Course;
import com.boda.springboot.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 课程管理 Controller
 */
@RestController
@Slf4j
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    /**
     * 添加课程
     * @param course 课程信息
     * @return 添加结果
     */
    @PostMapping
    @RequireRole("ADMIN")
    public Result saveCourse(@RequestBody Course course) {
        log.info("接收到添加课程请求: {}", course);

        // 参数校验
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return Result.error("课程名称不能为空");
        }
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            return Result.error("课程编号不能为空");
        }
        if (course.getCollege() == null || course.getCollege().trim().isEmpty()) {
            return Result.error("开课学院不能为空");
        }

        // 调用 Service 保存课程
        courseService.saveCourse(course);
        return Result.success("课程添加成功!");
    }

    /**
     * 分页查询课程列表
     * @param coursePageQueryDTO 查询条件（包含分页参数）
     * @return 分页结果
     */
    @GetMapping("/page")
    @RequireRole({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT})
    public Result<PageResult> coursePageQuery(CoursePageQueryDTO coursePageQueryDTO) {
        log.info("接收到课程分页查询请求: {}", coursePageQueryDTO);

        // 调用 Service 层执行分页查询
        PageResult pageResult = courseService.pageQuery(coursePageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 根据课程ID查询课程信息
     * @param courseId 课程ID
     * @return 课程信息
     */
    @GetMapping("/{courseId}")
    public Result<Course> getCourseById(@PathVariable Long courseId) {
        log.info("接收到根据ID查询课程请求: {}", courseId);
        // 调用 Service 层获取课程信息
        Course course = courseService.getCourseById(courseId);

        return Result.success(course);
    }

    /**
     * 更新课程信息
     * @param course 课程信息
     * @param courseId 课程ID
     * @return 更新结果
     */
    @PutMapping("/{courseId}")
    @RequireRole("ADMIN")
    public Result updateCourse(@RequestBody Course course, @PathVariable Long courseId) {
        log.info("接收到更新课程请求 - ID: {}, 数据: {}", courseId, course);

        // 设置课程ID（确保更新的是正确的课程）
        course.setCourseId(courseId);

        // 调用 Service 层更新课程
        courseService.updateCourse(course);

        return Result.success("课程更新成功!");
    }

    /**
     * 删除课程（管理员）
     * DELETE /courses/1
     */
    @DeleteMapping("/{courseId}")
    @RequireRole(Constant.ROLE_ADMIN)
    public Result deleteCourse(@PathVariable Long courseId) {
        log.info("接收到删除课程请求 - 课程ID: {}", courseId);

        courseService.deleteCourse(courseId);

        return Result.success("课程删除成功!");
    }

    /**
     * 为课程分配教师（管理员）
     * POST /courses/1/teachers
     * Body: {"teacherId": 123}
     */
    @PostMapping("/{courseId}/teachers")
    @RequireRole(Constant.ROLE_ADMIN)
    public Result assignTeacher(@PathVariable Long courseId, @RequestBody Map<String, Long> params) {
        log.info("接收到分配教师请求 - 课程ID: {}, 参数: {}", courseId, params);

        Long teacherId = params.get("teacherId");
        if (teacherId == null) {
            return Result.error("教师ID不能为空");
        }

        courseService.assignTeacher(courseId, teacherId);

        return Result.success("教师分配成功!");
    }

    /**
     * 移除课程教师（管理员）
     * DELETE /courses/1/teachers/2
     */
    @DeleteMapping("/{courseId}/teachers/{teacherId}")
    @RequireRole(Constant.ROLE_ADMIN)
    public Result removeTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        log.info("接收到移除教师请求 - 课程ID: {}, 教师ID: {}", courseId, teacherId);

        courseService.removeTeacher(courseId, teacherId);

        return Result.success("教师移除成功!");
    }

    /**
     * 获取我的课程列表（教师）
     * GET /courses/my?pageNum=1&pageSize=10
     */
    @GetMapping("/my")
    @RequireRole(Constant.ROLE_TEACHER)
    public Result<PageResult> getMyCourses(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {

        log.info("接收到查询我的课程列表请求");

        Long teacherId = (Long) request.getAttribute("userId");
        PageResult pageResult = courseService.getMyCourses(teacherId, pageNum, pageSize);

        return Result.success(pageResult);
    }

    /**
     * 学生加入课程（通过邀请码）
     * POST /courses/join
     * Body: {"inviteCode": "ABC123"}
     */
    @PostMapping("/join")
    @RequireRole(Constant.ROLE_STUDENT)
    public Result joinCourse(@RequestBody Map<String, String> params, HttpServletRequest request) {
        log.info("接收到学生加入课程请求 - 参数: {}", params);

        String inviteCode = params.get("inviteCode");
        if (inviteCode == null || inviteCode.trim().isEmpty()) {
            return Result.error("邀请码不能为空");
        }

        Long studentId = (Long) request.getAttribute("userId");
        courseService.joinCourse(studentId, inviteCode);

        return Result.success("加入课程成功!");
    }

}
