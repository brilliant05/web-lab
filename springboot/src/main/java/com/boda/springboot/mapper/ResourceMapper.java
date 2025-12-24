package com.boda.springboot.mapper;

import com.boda.springboot.dto.ResourcePageQueryDTO;
import com.boda.springboot.entity.Resource;
import com.boda.springboot.vo.HotResourceVO;
import com.boda.springboot.vo.ResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资源Mapper
 */
@Mapper
public interface ResourceMapper {

    /**
     * 保存资源
     */
    void save(Resource resource);

    /**
     * 更新资源
     */
    void update(Resource resource);

    /**
     * 根据ID删除（逻辑删除）
     */
    void deleteById(Long resourceId);

    /**
     * 根据ID查询
     */
    Resource selectById(Long resourceId);

    /**
     * 根据ID查询（包含已删除）
     */
    Resource selectAnyById(Long resourceId);

    /**
     * 分页查询资源列表（带关联信息）
     */
    List<ResourceVO> selectPageList(ResourcePageQueryDTO queryDTO);

    /**
     * 查询我上传的资源
     */
    List<ResourceVO> selectMyUploads(@Param("uploaderId") Long uploaderId, 
                                   @Param("resourceTitle") String resourceTitle, 
                                   @Param("courseId") Long courseId);

    /**
     * 增加下载次数
     */
    @Select("UPDATE resource SET download_count = download_count + 1 WHERE resource_id = #{resourceId}")
    void incrementDownloadCount(Long resourceId);

    /**
     * 增加浏览次数
     */
    @Select("UPDATE resource SET view_count = view_count + 1 WHERE resource_id = #{resourceId}")
    void incrementViewCount(Long resourceId);

    /**
     * 更新置顶状态
     */
    void updateTopStatus(@Param("resourceId") Long resourceId, @Param("isTop") Integer isTop);

    /**
     * 按ID联查详情（包含课程名与上传者姓名）
     */
    ResourceVO selectDetailById(@Param("resourceId") Long resourceId);

    /**
     * 统计资源总数
     * @return 资源总数
     */
    @Select("SELECT COUNT(*) FROM resource WHERE is_deleted = 0 AND status = 1")
    Integer countResources();

    /**
     * 查询回收站资源
     */
    List<ResourceVO> selectRecycleBinList(@Param("uploaderId") Long uploaderId);

    /**
     * 恢复资源
     */
    void restoreById(Long resourceId);

    /**
     * 永久删除资源
     */
    void deletePermanentlyById(Long resourceId);

    /**
     * 查询热门资源（按下载次数、浏览次数排序）
     */
    List<HotResourceVO> selectHotResources(@Param("limit") Integer limit);
}
