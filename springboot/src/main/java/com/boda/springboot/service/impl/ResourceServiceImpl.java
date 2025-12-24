package com.boda.springboot.service.impl;

import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.dto.ResourcePageQueryDTO;
import com.boda.springboot.dto.ResourceUpdateDTO;
import com.boda.springboot.dto.ResourceUploadDTO;
import com.boda.springboot.entity.Resource;
import com.boda.springboot.entity.ResourceCollection;
import com.boda.springboot.entity.StudentCourse;
import com.boda.springboot.exception.ServiceException;
import com.boda.springboot.mapper.ResourceCollectionMapper;
import com.boda.springboot.mapper.ResourceMapper;
import com.boda.springboot.mapper.StudentCourseMapper;
import com.boda.springboot.service.ResourceService;
import com.boda.springboot.utils.FileTypeValidator;
import com.boda.springboot.utils.QiNiuUtil;
import com.boda.springboot.vo.ResourceVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源Service实现类
 */
@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private ResourceCollectionMapper collectionMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private QiNiuUtil qiNiuUtil;

    /**
     * 允许上传的文件类型（MIME类型）
     */
    @Value("#{'${upload.allowed-types}'.split(',')}")
    private List<String> allowedTypes;

    /**
     * 允许上传的文件扩展名
     */
    @Value("#{'${upload.allowed-extensions}'.split(',')}")
    private List<String> allowedExtensions;

    /**
     * 上传资源
     */
    @Override
    @Transactional
    public void uploadResource(MultipartFile file, ResourceUploadDTO uploadDTO, Long uploaderId) {
        log.info("开始上传资源 - 文件名: {}, 上传者ID: {}", file.getOriginalFilename(), uploaderId);
        try {
            // 1. 校验文件
            if (file.isEmpty()) {
                throw new ServiceException("文件不能为空");
            }
            // 2. 校验文件类型
            validateFileType(file);
            // 3. 上传到七牛云
            String fileName = "resource/" + uploadDTO.getCourseId() + "/"
                + qiNiuUtil.generateUniqueFileName(file.getOriginalFilename());
            String fileUrl = qiNiuUtil.uploadFile(file, fileName);
            // 4. 保存资源记录
            Resource resource = new Resource();
            // 复制 DTO 中的字段，如为空则使用数据库默认值
            BeanUtils.copyProperties(uploadDTO, resource);
            // 设置文件相关字段
            //上传者ID
            resource.setUploaderId(uploaderId);
            //原始文件名
            resource.setFileName(file.getOriginalFilename());
            //文件URL
            resource.setFilePath(fileUrl);
            //文件大小
            resource.setFileSize(file.getSize());
            //文件类型
            resource.setFileType(getFileTypeFromContentType(file.getContentType()));
            //保存资源
            resourceMapper.save(resource);
            log.info("资源上传成功 - 资源ID: {}, 文件URL: {}", resource.getResourceId(), fileUrl);

        } catch (IOException e) {
            log.error("资源上传失败 - 文件名: {}", file.getOriginalFilename(), e);
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }
    /**
     * 分页查询资源
     */
    @Override
    public PageResult pageQuery(ResourcePageQueryDTO queryDTO, Long currentUserId, String role) {
        // 如果是学生，且查询指定课程的资源
        if (Constant.ROLE_STUDENT.equals(role) && queryDTO.getCourseId() != null) {
            // 检查学生是否加入该课程
            StudentCourse studentCourse = studentCourseMapper.selectByStudentAndCourse(currentUserId, queryDTO.getCourseId());
            // 如果未加入课程，只能查看公开资源
            if (studentCourse == null) {
                queryDTO.setVisibility(Constant.RESOURCE_PUBLIC);
            }
        }

        // 启动分页
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 查询数据
        Page<ResourceVO> page = (Page<ResourceVO>) resourceMapper.selectPageList(queryDTO);
        // 设置查询的资源是否被当前用户收藏，若被收藏则设置 isCollected 为 true
        if (currentUserId != null && !page.isEmpty()) {
            List<Long> resourceIds = page.stream()
                    .map(ResourceVO::getResourceId)
                    .collect(Collectors.toList());
            List<Long> collectedIds = collectionMapper.selectCollectedResourceIds(currentUserId, resourceIds);
            page.forEach(resource -> {
                resource.setIsCollected(collectedIds.contains(resource.getResourceId()));
            });
        }
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 获取资源详情
     */
    @Override
    public ResourceVO getResourceById(Long resourceId, Long currentUserId, String role) {
        log.info("查询资源详情 - 资源ID: {}", resourceId);
        // 使用联查获取详情（包含课程名和上传者姓名）
        ResourceVO vo = resourceMapper.selectDetailById(resourceId);
        if (vo == null) {
            throw new ServiceException("资源不存在或已下架");
        }

        // 权限检查：如果是学生，且资源是课程私有，检查是否加入课程
        if (Constant.ROLE_STUDENT.equals(role) && Constant.RESOURCE_COURSE_ONLY.equals(vo.getVisibility())) {
            StudentCourse studentCourse = studentCourseMapper.selectByStudentAndCourse(currentUserId, vo.getCourseId());
            if (studentCourse == null) {
                throw new ServiceException("无权访问该课程的私有资源");
            }
        }

        // 增加浏览次数
        resourceMapper.incrementViewCount(resourceId);
        // 查询收藏状态
        if (currentUserId != null) {
            ResourceCollection collection = collectionMapper.selectByUserAndResource(currentUserId, resourceId);
            vo.setIsCollected(collection != null);
        }
        return vo;
    }
    /**
     * 更新资源信息
     */
    @Override
    @Transactional
    public void updateResource(Long resourceId, ResourceUpdateDTO updateDTO, Long currentUserId) {
        log.info("更新资源信息 - 资源ID: {}, 更新数据: {}", resourceId, updateDTO);

        // 1. 查询资源
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }

        // 2. 权限校验：只有上传者或管理员可以修改（这里简化处理，实际应从request获取role）
        if (!resource.getUploaderId().equals(currentUserId)) {
            throw new ServiceException("无权修改此资源");
        }

        // 3. 更新数据
        Resource updateResource = new Resource();
        updateResource.setResourceId(resourceId);
        updateResource.setResourceTitle(updateDTO.getResourceTitle());
        updateResource.setDescription(updateDTO.getDescription());
        updateResource.setTags(updateDTO.getTags());
        updateResource.setVisibility(updateDTO.getVisibility());
        updateResource.setIsTop(updateDTO.getIsTop());

        resourceMapper.update(updateResource);
        log.info("资源更新成功 - 资源ID: {}", resourceId);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId, Long currentUserId) {
        log.info("删除资源 - 资源ID: {}", resourceId);

        // 1. 查询资源
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }

        // 2. 权限校验：只有上传者或管理员可以删除
        if (!resource.getUploaderId().equals(currentUserId)) {
            throw new ServiceException("无权删除此资源");
        }

        // 3. 逻辑删除
        resourceMapper.deleteById(resourceId);
        log.info("资源删除成功 - 资源ID: {}", resourceId);
    }

    @Override
    public String downloadResource(Long resourceId, Long currentUserId, String role) {
        log.info("下载资源 - 资源ID: {}", resourceId);

        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }

        // 权限检查：如果是学生，且资源是课程私有，检查是否加入课程
        if (Constant.ROLE_STUDENT.equals(role) && Constant.RESOURCE_COURSE_ONLY.equals(resource.getVisibility())) {
            StudentCourse studentCourse = studentCourseMapper.selectByStudentAndCourse(currentUserId, resource.getCourseId());
            if (studentCourse == null) {
                throw new ServiceException("无权下载该课程的私有资源");
            }
        }

        // 增加下载次数
        resourceMapper.incrementDownloadCount(resourceId);

        return resource.getFilePath();
    }

    @Override
    @Transactional
    public void collectResource(Long resourceId, Long studentId) {
        log.info("收藏资源 - 资源ID: {}, 学生ID: {}", resourceId, studentId);

        // 1. 检查资源是否存在
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }

        // 2. 检查是否已收藏
        ResourceCollection existCollection = collectionMapper.selectByUserAndResource(studentId, resourceId);
        if (existCollection != null) {
            throw new ServiceException("已经收藏过此资源");
        }

        // 3. 添加收藏
        ResourceCollection collection = new ResourceCollection();
        collection.setStudentId(studentId);
        collection.setResourceId(resourceId);
        collectionMapper.save(collection);

        log.info("收藏成功 - 资源ID: {}", resourceId);
    }

    @Override
    @Transactional
    public void uncollectResource(Long resourceId, Long studentId) {

        collectionMapper.delete(studentId, resourceId);
        log.info("取消收藏成功 - 资源ID: {}", resourceId);
    }

    @Override
    public PageResult getMyCollections(Integer pageNum, Integer pageSize, Long studentId) {
        log.info("查询我的收藏列表 - 学生ID: {}", studentId);

        PageHelper.startPage(pageNum, pageSize);
        Page<ResourceVO> page = (Page<ResourceVO>) collectionMapper.selectMyCollections(studentId);

        // 所有收藏的资源都标记为已收藏
        page.forEach(resource -> resource.setIsCollected(true));

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public PageResult getMyUploads(Integer pageNum, Integer pageSize, Long uploaderId, String resourceTitle, Long courseId) {
        log.info("查询我上传的资源 - 上传者ID: {}, 标题: {}, 课程ID: {}", uploaderId, resourceTitle, courseId);

        PageHelper.startPage(pageNum, pageSize);
        Page<ResourceVO> page = (Page<ResourceVO>) resourceMapper.selectMyUploads(uploaderId, resourceTitle, courseId);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void topResource(Long resourceId, Integer isTop, Long currentUserId) {
        log.info("设置资源置顶 - 资源ID: {}, 置顶状态: {}", resourceId, isTop);

        // 1. 查询资源
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }

        // 2. 权限校验：这里简化处理，由Controller的@RequireRole注解保证权限

        // 3. 更新置顶状态
        resourceMapper.updateTopStatus(resourceId, isTop);
        log.info("资源置顶设置成功 - 资源ID: {}", resourceId);
    }

    @Override
    public PageResult getRecycleBinList(Integer pageNum, Integer pageSize, Long uploaderId) {
        PageHelper.startPage(pageNum, pageSize);
        List<ResourceVO> list = resourceMapper.selectRecycleBinList(uploaderId);
        Page<ResourceVO> page = (Page<ResourceVO>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void restoreResource(Long resourceId, Long currentUserId) {
        Resource resource = resourceMapper.selectAnyById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }
        // 简单权限校验：仅上传者可恢复
        if (!resource.getUploaderId().equals(currentUserId)) {
             throw new ServiceException("无权操作此资源");
        }
        resourceMapper.restoreById(resourceId);
    }

    @Override
    @Transactional
    public void deleteResourcePermanently(Long resourceId, Long currentUserId) {
        Resource resource = resourceMapper.selectAnyById(resourceId);
        if (resource == null) {
            throw new ServiceException("资源不存在");
        }
        // 简单权限校验：仅上传者可删除
        if (!resource.getUploaderId().equals(currentUserId)) {
             throw new ServiceException("无权操作此资源");
        }
        
        // TODO: 如果需要，在此处调用七牛云工具类删除云端文件
        
        resourceMapper.deletePermanentlyById(resourceId);
    }

    /**
     * 根据 MIME 类型获取文件类型
     */
    private String getFileTypeFromContentType(String contentType) {
        if (contentType == null) {
            return "OTHER";
        }

        if (contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType.startsWith("video/")) {
            return "VIDEO";
        } else if (contentType.equals("application/pdf")) {
            return "PDF";
        } else if (contentType.contains("powerpoint") || contentType.contains("presentation")) {
            return "PPT";
        } else if (contentType.contains("excel") || contentType.contains("spreadsheet") || contentType.contains("sheet")) {
            return "EXCEL";
        } else if (contentType.contains("word") || contentType.contains("document")) {
            return "WORD";
        } else if (contentType.contains("zip") || contentType.contains("rar") || contentType.contains("7z")) {
            return "ZIP";
        } else {
            return "OTHER";
        }
    }

    /**
     * 校验文件类型
     * @param file 上传的文件
     */
    private void validateFileType(MultipartFile file) {
        // 使用工具类进行校验（非严格模式，只记录 MIME 警告）
        FileTypeValidator.validateFileType(file, allowedExtensions, allowedTypes, false);
    }
}

