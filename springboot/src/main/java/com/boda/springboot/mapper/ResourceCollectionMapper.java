package com.boda.springboot.mapper;

import com.boda.springboot.entity.ResourceCollection;
import com.boda.springboot.vo.ResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源收藏Mapper
 */
@Mapper
public interface ResourceCollectionMapper {

    /**
     * 添加收藏
     */
    void save(ResourceCollection collection);

    /**
     * 取消收藏
     */
    void delete(@Param("studentId") Long studentId, @Param("resourceId") Long resourceId);

    /**
     * 查询是否已收藏
     */
    ResourceCollection selectByUserAndResource(@Param("studentId") Long studentId,
                                               @Param("resourceId") Long resourceId);

    /**
     * 查询我的收藏列表
     */
    List<ResourceVO> selectMyCollections(@Param("studentId") Long studentId);

    /**
     * 批量查询收藏状态
     */
    List<Long> selectCollectedResourceIds(@Param("studentId") Long studentId,
                                          @Param("resourceIds") List<Long> resourceIds);
}

