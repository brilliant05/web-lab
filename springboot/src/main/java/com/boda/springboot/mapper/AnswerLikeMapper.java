package com.boda.springboot.mapper;

import com.boda.springboot.entity.AnswerLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 回答点赞Mapper
 */
@Mapper
public interface AnswerLikeMapper {

    /**
     * 添加点赞
     */
    void save(AnswerLike answerLike);

    /**
     * 取消点赞
     */
    void delete(@Param("answerId") Long answerId, @Param("userId") Long userId);

    /**
     * 查询是否已点赞
     */
    AnswerLike selectByAnswerAndUser(@Param("answerId") Long answerId, @Param("userId") Long userId);

    /**
     * 批量查询点赞状态
     */
    List<Long> selectLikedAnswerIds(@Param("userId") Long userId, @Param("answerIds") List<Long> answerIds);
}

