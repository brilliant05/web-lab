package com.boda.springboot.mapper;

import com.boda.springboot.entity.Question;
import com.boda.springboot.vo.QuestionDetailVO;
import com.boda.springboot.vo.QuestionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问题Mapper
 */
@Mapper
public interface QuestionMapper {

    /**
     * 保存问题
     */
    void save(Question question);

    /**
     * 更新问题
     */
    void update(Question question);

    /**
     * 根据ID删除（逻辑删除）
     */
    void deleteById(Long questionId);

    /**
     * 根据ID查询
     */
    Question selectById(Long questionId);

    /**
     * 分页查询问题列表（带关联信息）
     */
    List<QuestionVO> selectPageList(@Param("courseId") Long courseId,
                                    @Param("teacherId") Long teacherId,
                                    @Param("isAnswered") Integer isAnswered,
                                    @Param("keyword") String keyword);

    /**
     * 查询我的提问列表
     */
    List<QuestionVO> selectMyQuestions(@Param("studentId") Long studentId,
                                       @Param("isAnswered") Integer isAnswered);

    /**
     * 查询教师待回答问题列表
     */
    List<QuestionVO> selectPendingQuestions(@Param("teacherId") Long teacherId,
                                            @Param("courseId") Long courseId);

    /**
     * 查询问题详情（包含关联信息）
     */
    QuestionDetailVO selectDetailById(Long questionId);

    /**
     * 增加浏览次数
     */
    void increaseViewCount(Long questionId);

    /**
     * 增加回答数量
     */
    void increaseAnswerCount(Long questionId);

    /**
     * 减少回答数量
     */
    void decreaseAnswerCount(Long questionId);

    /**
     * 更新回答状态
     */
    void updateAnsweredStatus(@Param("questionId") Long questionId, @Param("isAnswered") Integer isAnswered);
}

