package com.boda.springboot.service;

import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.ResourcePageQueryDTO;
import com.boda.springboot.dto.ResourceUpdateDTO;
import com.boda.springboot.dto.ResourceUploadDTO;
import com.boda.springboot.entity.Resource;
import com.boda.springboot.vo.ResourceVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资源Service接口
 */
public interface ResourceService {

    /**
     * 上传资源
     */
    void uploadResource(MultipartFile file, ResourceUploadDTO uploadDTO, Long uploaderId);

    /**
     * 分页查询资源列表
     */
    PageResult pageQuery(ResourcePageQueryDTO queryDTO, Long currentUserId, String role);

    /**
     * 获取资源详情
     */
    ResourceVO getResourceById(Long resourceId, Long currentUserId, String role);

    /**
     * 更新资源信息
     */
    void updateResource(Long resourceId, ResourceUpdateDTO updateDTO, Long currentUserId);

    /**
     * 删除资源
     */
    void deleteResource(Long resourceId, Long currentUserId);

    /**
     * 下载资源（增加下载次数并返回文件URL）
     */
    String downloadResource(Long resourceId, Long currentUserId, String role);

    /**
     * 收藏资源
     */
    void collectResource(Long resourceId, Long studentId);

    /**
     * 取消收藏
     */
    void uncollectResource(Long resourceId, Long studentId);

    /**
     * 查询我的收藏列表
     */
    PageResult getMyCollections(Integer pageNum, Integer pageSize, Long studentId);

    /**
     * 查询我上传的资源
     */
    PageResult getMyUploads(Integer pageNum, Integer pageSize, Long uploaderId);

    /**
     * 置顶资源
     */
    void topResource(Long resourceId, Integer isTop, Long currentUserId);

    /**
     * 获取回收站资源列表
     */
    PageResult getRecycleBinList(Integer pageNum, Integer pageSize, Long uploaderId);

    /**
     * 恢复资源
     */
    void restoreResource(Long resourceId, Long currentUserId);

    /**
     * 永久删除资源
     */
    void deleteResourcePermanently(Long resourceId, Long currentUserId);
}

