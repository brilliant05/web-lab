package com.boda.springboot.mapper;

import com.boda.springboot.entity.Answer;
import com.boda.springboot.vo.AnswerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 回答Mapper
 */
@Mapper
public interface AnswerMapper {

    /**
     * 保存回答
     */
    void save(Answer answer);

    /**
     * 更新回答
     */
    void update(Answer answer);

    /**
     * 根据ID删除（逻辑删除）
     */
    void deleteById(Long answerId);

    /**
     * 根据ID查询
     */
    Answer selectById(Long answerId);

    /**
     * 根据问题ID查询所有回答
     */
    List<AnswerVO> selectByQuestionId(@Param("questionId") Long questionId,
                                      @Param("currentUserId") Long currentUserId);

    /**
     * 查询教师的所有回答
     */
    List<AnswerVO> selectByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 增加点赞数
     */
    void increaseLikeCount(Long answerId);

    /**
     * 减少点赞数
     */
    void decreaseLikeCount(Long answerId);
}

