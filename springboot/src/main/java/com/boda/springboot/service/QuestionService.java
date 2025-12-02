package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.AnswerSubmitDTO;
import com.boda.springboot.dto.QuestionPageQueryDTO;
import com.boda.springboot.dto.QuestionSubmitDTO;
import com.boda.springboot.dto.QuestionUpdateDTO;
import com.boda.springboot.vo.QuestionDetailVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 问答Service接口
 */
public interface QuestionService {

    /**
     * 提交问题 (学生)
     */
    void submitQuestion(QuestionSubmitDTO submitDTO, MultipartFile[] images, Long studentId);

    /**
     * 分页查询问题列表
     */
    PageResult pageQuery(QuestionPageQueryDTO queryDTO);

    /**
     * 获取问题详情
     */
    QuestionDetailVO getQuestionDetail(Long questionId, Long currentUserId);

    /**
     * 更新问题 (学生，仅未回答的问题；管理员可修改任意问题)
     */
    void updateQuestion(Long questionId, QuestionUpdateDTO updateDTO, Long userId, String userRole);

    /**
     * 删除问题 (学生只能删除自己的；管理员可删除任意问题)
     */
    void deleteQuestion(Long questionId, Long userId, String userRole);

    /**
     * 查询我的提问列表 (学生)
     */
    PageResult getMyQuestions(Integer pageNum, Integer pageSize, Integer isAnswered, Long studentId);

    /**
     * 查询待回答问题列表 (教师)
     */
    PageResult getPendingQuestions(Integer pageNum, Integer pageSize, Long courseId, Long teacherId);

    /**
     * 回答问题 (教师)
     */
    void answerQuestion(Long questionId, AnswerSubmitDTO submitDTO, MultipartFile[] images, Long teacherId);

    /**
     * 更新回答 (教师只能修改自己的；管理员可修改任意回答)
     */
    void updateAnswer(Long answerId, String answerContent, Long userId, String userRole);

    /**
     * 删除回答 (教师只能删除自己的；管理员可删除任意回答)
     */
    void deleteAnswer(Long answerId, Long userId, String userRole);

    /**
     * 查询我的回答列表 (教师)
     */
    PageResult getMyAnswers(Integer pageNum, Integer pageSize, Long teacherId);

    /**
     * 点赞回答
     */
    void likeAnswer(Long answerId, Long userId);

    /**
     * 取消点赞
     */
    void unlikeAnswer(Long answerId, Long userId);
}

