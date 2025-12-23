package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.common.Result;
import com.boda.springboot.dto.AnswerSubmitDTO;
import com.boda.springboot.dto.QuestionPageQueryDTO;
import com.boda.springboot.dto.QuestionSubmitDTO;
import com.boda.springboot.dto.QuestionUpdateDTO;
import com.boda.springboot.service.QuestionService;
import com.boda.springboot.vo.QuestionDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 问答管理Controller
 */
@RestController
@RequestMapping("/questions")
@Slf4j
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 获取问题列表（分页）
     * GET /questions?pageNum=1&pageSize=10
     * 可选查询参数见 QuestionPageQueryDTO
     */
    @GetMapping
    public Result<PageResult> getQuestionList(QuestionPageQueryDTO queryDTO, HttpServletRequest request) {
        log.info("接收到问题列表查询请求 - 查询条件: {}", queryDTO);

        // 获取当前用户信息
        Long currentUserId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        // 如果是教师，只能查看提问给自己的问题
        if (Constant.ROLE_TEACHER.equals(role)) {
            queryDTO.setTeacherId(currentUserId);
        }

        PageResult pageResult = questionService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取问题详情
     * GET /questions/1
     *
     */
    @GetMapping("/{questionId}")
    public Result<QuestionDetailVO> getQuestionDetail(@PathVariable Long questionId, HttpServletRequest request) {
        log.info("接收到问题详情查询请求 - 问题ID: {}", questionId);

        Long currentUserId = (Long) request.getAttribute("userId");
        QuestionDetailVO detail = questionService.getQuestionDetail(questionId, currentUserId);

        return Result.success(detail);
    }

    /**
     * 提交问题（学生）
     * POST /questions
     * Content-Type: multipart/form-data
     */
    @PostMapping
    @RequireRole(Constant.ROLE_STUDENT)
    public Result<Void> submitQuestion(
            @RequestParam("questionTitle") String questionTitle,
            @RequestParam("questionContent") String questionContent,
            @RequestParam("courseId") Long courseId,
            @RequestParam("teacherId") Long teacherId,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            HttpServletRequest request) {

        log.info("接收到提交问题请求 - 标题: {}, 课程ID: {}, 教师ID: {}", questionTitle, courseId, teacherId);

        QuestionSubmitDTO submitDTO = new QuestionSubmitDTO();
        submitDTO.setQuestionTitle(questionTitle);
        submitDTO.setQuestionContent(questionContent);
        submitDTO.setCourseId(courseId);
        submitDTO.setTeacherId(teacherId);
        submitDTO.setTags(tags);

        Long studentId = (Long) request.getAttribute("userId");
        questionService.submitQuestion(submitDTO, images, studentId);

        return Result.success("问题提交成功");
    }

    /**
     * 更新问题（学生、管理员）
     * PUT /questions/1
     */
    @PutMapping("/{questionId}")
    @RequireRole({Constant.ROLE_STUDENT, Constant.ROLE_ADMIN})
    public Result<Void> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateDTO updateDTO,
            HttpServletRequest request) {

        log.info("接收到更新问题请求 - 问题ID: {}, 更新数据: {}", questionId, updateDTO);

        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("role");
        questionService.updateQuestion(questionId, updateDTO, userId, userRole);

        return Result.success("问题更新成功");
    }

    /**
     * 删除问题（学生、管理员）
     * DELETE /questions/1
     */
    @DeleteMapping("/{questionId}")
    @RequireRole({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT})
    public Result<Void> deleteQuestion(@PathVariable Long questionId, HttpServletRequest request) {
        log.info("接收到删除问题请求 - 问题ID: {}", questionId);

        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("role");
        questionService.deleteQuestion(questionId, userId, userRole);

        return Result.success("问题删除成功");
    }

    /**
     * 我的提问列表（学生）
     * GET /questions/my-questions?pageNum=1&pageSize=10&isAnswered=1
     */
    @GetMapping("/my-questions")
    @RequireRole(Constant.ROLE_STUDENT)
    public Result<PageResult> getMyQuestions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer isAnswered,
            HttpServletRequest request) {

        log.info("接收到查询我的提问列表请求 - 页码: {}, 每页数量: {}", pageNum, pageSize);

        Long studentId = (Long) request.getAttribute("userId");
        PageResult pageResult = questionService.getMyQuestions(pageNum, pageSize, isAnswered, studentId);

        return Result.success(pageResult);
    }

    /**
     * 待回答问题列表（教师）
     * GET /questions/pending?pageNum=1&pageSize=10&courseId=1
     */
    @GetMapping("/pending")
    @RequireRole(Constant.ROLE_TEACHER)
    public Result<PageResult> getPendingQuestions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId,
            HttpServletRequest request) {

        log.info("接收到查询待回答问题列表请求 - 页码: {}, 每页数量: {}", pageNum, pageSize);

        Long teacherId = (Long) request.getAttribute("userId");
        PageResult pageResult = questionService.getPendingQuestions(pageNum, pageSize, courseId, teacherId);

        return Result.success(pageResult);
    }

    /**
     * 回答问题（教师）
     * POST /questions/1/answers
     * Content-Type: multipart/form-data
     */
    @PostMapping("/{questionId}/answers")
    @RequireRole(Constant.ROLE_TEACHER)
    public Result<Void> answerQuestion(
            @PathVariable Long questionId,
            @RequestParam("answerContent") String answerContent,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            HttpServletRequest request) {

        log.info("接收到回答问题请求 - 问题ID: {}", questionId);

        AnswerSubmitDTO submitDTO = new AnswerSubmitDTO();
        submitDTO.setAnswerContent(answerContent);

        Long teacherId = (Long) request.getAttribute("userId");
        questionService.answerQuestion(questionId, submitDTO, images, teacherId);

        return Result.success("回答成功");
    }

    /**
     * 更新回答（教师、管理员）
     * PUT /answers/1
     */
    @PutMapping("/answers/{answerId}")
    @RequireRole({Constant.ROLE_TEACHER, Constant.ROLE_ADMIN})
    public Result<Void> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody Map<String, String> params,
            HttpServletRequest request) {

        log.info("接收到更新回答请求 - 回答ID: {}", answerId);

        String answerContent = params.get("answerContent");
        if (answerContent == null || answerContent.trim().isEmpty()) {
            return Result.error("回答内容不能为空");
        }

        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("role");
        questionService.updateAnswer(answerId, answerContent, userId, userRole);

        return Result.success("回答更新成功");
    }

    /**
     * 删除回答（教师、管理员）
     * DELETE /answers/1
     */
    @DeleteMapping("/answers/{answerId}")
    @RequireRole({Constant.ROLE_TEACHER, Constant.ROLE_ADMIN})
    public Result<Void> deleteAnswer(@PathVariable Long answerId, HttpServletRequest request) {
        log.info("接收到删除回答请求 - 回答ID: {}", answerId);

        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("role");
        questionService.deleteAnswer(answerId, userId, userRole);

        return Result.success("回答删除成功");
    }

    /**
     * 我的回答列表（教师）
     * GET /answers/my-answers?pageNum=1&pageSize=10
     */
    @GetMapping("/answers/my-answers")
    @RequireRole(Constant.ROLE_TEACHER)
    public Result<PageResult> getMyAnswers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {

        log.info("接收到查询我的回答列表请求 - 页码: {}, 每页数量: {}", pageNum, pageSize);

        Long teacherId = (Long) request.getAttribute("userId");
        PageResult pageResult = questionService.getMyAnswers(pageNum, pageSize, teacherId);

        return Result.success(pageResult);
    }

    /**
     * 点赞回答
     * POST /answers/1/like
     *
     * 权限：所有登录用户
     */
    @PostMapping("/answers/{answerId}/like")
    public Result<Void> likeAnswer(@PathVariable Long answerId, HttpServletRequest request) {
        log.info("接收到点赞回答请求 - 回答ID: {}", answerId);

        Long userId = (Long) request.getAttribute("userId");
        questionService.likeAnswer(answerId, userId);

        return Result.success("点赞成功");
    }

    /**
     * 取消点赞
     * DELETE /answers/1/like
     *
     * 权限：所有登录用户
     */
    @DeleteMapping("/answers/{answerId}/like")
    public Result<Void> unlikeAnswer(@PathVariable Long answerId, HttpServletRequest request) {
        log.info("接收到取消点赞请求 - 回答ID: {}", answerId);

        Long userId = (Long) request.getAttribute("userId");
        questionService.unlikeAnswer(answerId, userId);

        return Result.success("取消点赞成功");
    }
}

