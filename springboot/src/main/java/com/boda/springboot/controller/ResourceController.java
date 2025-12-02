package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.PageResult;
import com.boda.springboot.common.Result;
import com.boda.springboot.dto.ResourcePageQueryDTO;
import com.boda.springboot.dto.ResourceUpdateDTO;
import com.boda.springboot.dto.ResourceUploadDTO;
import com.boda.springboot.service.ResourceService;
import com.boda.springboot.vo.ResourceVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 学习资源 Controller
 */
@RestController
@RequestMapping("/resources")
@Slf4j
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 获取资源列表（分页）
     * GET /resources?pageNum=1&pageSize=10
     * 可选查询参数见 ResourcePageQueryDTO
     *
     * 权限：所有登录用户
     */
    @GetMapping
    public Result<PageResult> getResourceList(ResourcePageQueryDTO queryDTO, HttpServletRequest request) {
        log.info("接收到资源列表查询请求 - 查询条件: {}", queryDTO);

        Long currentUserId = (Long) request.getAttribute("userId");
        PageResult pageResult = resourceService.pageQuery(queryDTO, currentUserId);

        return Result.success(pageResult);
    }

    /**
     * 获取资源详情
     * GET /resources/1
     *
     * 权限：所有登录用户
     */
    @GetMapping("/{resourceId}")
    public Result<ResourceVO> getResourceDetail(@PathVariable Long resourceId, HttpServletRequest request) {
        log.info("接收到资源详情查询请求 - 资源ID: {}", resourceId);

        Long currentUserId = (Long) request.getAttribute("userId");
        ResourceVO resource = resourceService.getResourceById(resourceId, currentUserId);

        return Result.success(resource);
    }

    /**
     * 上传资源
     * POST /resources
     * Content-Type: multipart/form-data
     *
     * 参数说明:
     * - file: 文件（必填）
     * - resourceTitle: 资源标题（必填）
     * - description: 资源描述（可选）
     * - courseId: 课程ID（必填）
     * - visibility: 可见性 PUBLIC/COURSE_ONLY（可选，默认PUBLIC）
     * - tags: 标签，逗号分隔（可选）
     */
    @PostMapping
    public Result<Void> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("resourceTitle") String resourceTitle,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "visibility", required = false, defaultValue = "PUBLIC") String visibility,
            @RequestParam(value = "tags", required = false) String tags,
            HttpServletRequest request) {

        log.info("接收到资源上传请求 - 文件名: {}, 标题: {}, 课程ID: {}",
                file.getOriginalFilename(), resourceTitle, courseId);

        // 参数校验
        if (resourceTitle == null || resourceTitle.trim().isEmpty()) {
            return Result.error("资源标题不能为空");
        }
        if (courseId == null) {
            return Result.error("课程ID不能为空");
        }

        // 构建DTO
        ResourceUploadDTO uploadDTO = new ResourceUploadDTO();
        uploadDTO.setResourceTitle(resourceTitle);
        uploadDTO.setDescription(description);
        uploadDTO.setCourseId(courseId);
        uploadDTO.setVisibility(visibility);
        uploadDTO.setTags(tags);

        // 获取当前用户ID
        Long uploaderId = (Long) request.getAttribute("userId");

        // 上传资源
        resourceService.uploadResource(file, uploadDTO, uploaderId);

        return Result.success("资源上传成功");
    }

    /**
     * 更新资源信息
     * PUT /resources/1
     * Content-Type: application/json
     *
     * Body:
     * {
     *   "resourceTitle": "新标题",
     *   "description": "新描述",
     *   "tags": "新标签",
     *   "visibility": "PUBLIC",
     *   "isTop": 1
     * }
     */
    @PutMapping("/{resourceId}")
    public Result<Void> updateResource(
            @PathVariable Long resourceId,
            @RequestBody ResourceUpdateDTO updateDTO,
            HttpServletRequest request) {

        log.info("接收到资源更新请求 - 资源ID: {}, 更新数据: {}", resourceId, updateDTO);

        Long currentUserId = (Long) request.getAttribute("userId");
        resourceService.updateResource(resourceId, updateDTO, currentUserId);

        return Result.success("资源更新成功");
    }

    /**
     * 删除资源
     * DELETE /resources/1
     */
    @DeleteMapping("/{resourceId}")
    public Result<Void> deleteResource(@PathVariable Long resourceId, HttpServletRequest request) {
        log.info("接收到资源删除请求 - 资源ID: {}", resourceId);

        Long currentUserId = (Long) request.getAttribute("userId");
        resourceService.deleteResource(resourceId, currentUserId);

        return Result.success("资源删除成功");
    }

    /**
     * 下载资源
     * GET /resources/1/download
     *
     * 说明: 返回资源的文件URL，前端使用此URL下载文件
     * 权限：所有登录用户
     */
    @GetMapping("/{resourceId}/download")
    public Result<Map<String, String>> downloadResource(@PathVariable Long resourceId) {
        log.info("接收到资源下载请求 - 资源ID: {}", resourceId);

        String fileUrl = resourceService.downloadResource(resourceId);

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);

        return Result.success(result);
    }

    /**
     * 收藏资源
     * POST /resources/1/collect
     *
     * 权限：所有登录用户
     */
    @PostMapping("/{resourceId}/collect")
    public Result<Void> collectResource(@PathVariable Long resourceId, HttpServletRequest request) {
        log.info("接收到收藏资源请求 - 资源ID: {}", resourceId);

        Long studentId = (Long) request.getAttribute("userId");
        resourceService.collectResource(resourceId, studentId);

        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     * DELETE /resources/1/collect
     *
     * 权限：所有登录用户
     */
    @DeleteMapping("/{resourceId}/collect")
    public Result<Void> uncollectResource(@PathVariable Long resourceId, HttpServletRequest request) {
        log.info("接收到取消收藏请求 - 资源ID: {}", resourceId);

        Long studentId = (Long) request.getAttribute("userId");
        resourceService.uncollectResource(resourceId, studentId);

        return Result.success("取消收藏成功");
    }

    /**
     * 我的收藏列表
     * GET /resources/collections?pageNum=1&pageSize=10
     *
     * 权限：所有登录用户
     */
    @GetMapping("/collections")
    public Result<PageResult> getMyCollections(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {

        log.info("接收到查询我的收藏列表请求 - 页码: {}, 每页数量: {}", pageNum, pageSize);

        Long studentId = (Long) request.getAttribute("userId");
        PageResult pageResult = resourceService.getMyCollections(pageNum, pageSize, studentId);

        return Result.success(pageResult);
    }

    /**
     * 我上传的资源
     * GET /resources/my-uploads?pageNum=1&pageSize=10
     */
    @GetMapping("/my-uploads")
    public Result<PageResult> getMyUploads(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {

        log.info("接收到查询我上传的资源请求 - 页码: {}, 每页数量: {}", pageNum, pageSize);

        Long uploaderId = (Long) request.getAttribute("userId");
        PageResult pageResult = resourceService.getMyUploads(pageNum, pageSize, uploaderId);

        return Result.success(pageResult);
    }

    /**
     * 置顶资源（仅教师和管理员）
     * PUT /resources/1/top
     * Content-Type: application/json
     *
     * Body:
     * {
     *   "isTop": 1
     * }
     */
    @PutMapping("/{resourceId}/top")
    @RequireRole({Constant.ROLE_ADMIN, Constant.ROLE_TEACHER})
    public Result<Void> topResource(
            @PathVariable Long resourceId,
            @RequestBody Map<String, Integer> params,
            HttpServletRequest request) {

        log.info("接收到资源置顶请求 - 资源ID: {}, 参数: {}", resourceId, params);

        Integer isTop = params.get("isTop");
        if (isTop == null) {
            return Result.error("isTop参数不能为空");
        }

        Long currentUserId = (Long) request.getAttribute("userId");
        resourceService.topResource(resourceId, isTop, currentUserId);

        return Result.success("操作成功");
    }
}

