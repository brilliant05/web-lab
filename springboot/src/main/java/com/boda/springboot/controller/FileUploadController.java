package com.boda.springboot.controller;

import com.boda.springboot.annotation.RequireRole;
import com.boda.springboot.common.Constant;
import com.boda.springboot.common.Result;
import com.boda.springboot.utils.QiNiuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传 Controller
 */
@RestController
@Slf4j
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private QiNiuUtil qiNiuUtil;

    /**
     * 上传单个文件
     *
     * @param file 上传的文件
     * @return 上传结果（包含文件URL）
     */
    @PostMapping
    @RequireRole({Constant.ROLE_ADMIN, Constant.ROLE_TEACHER, Constant.ROLE_STUDENT})
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("接收到文件上传请求 - 文件名: {}, 大小: {} bytes",
                file.getOriginalFilename(), file.getSize());

        try {
            // 1. 参数校验
            if (file.isEmpty()) {
                log.warn("上传文件为空");
                return Result.error("文件不能为空");
            }

            // 2. 文件大小校验（限制10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                log.warn("文件大小超过限制: {} bytes", file.getSize());
                return Result.error("文件大小不能超过 10MB");
            }

            // 3. 文件类型校验（可选）
            String contentType = file.getContentType();
            if (contentType == null || !isAllowedFileType(contentType)) {
                log.warn("不支持的文件类型: {}", contentType);
                return Result.error("不支持的文件类型，仅支持图片、视频、PDF、Office 文档");
            }

            // 4. 上传到七牛云
            String fileUrl = qiNiuUtil.uploadFile(file);
            log.info("文件上传成功: {}", fileUrl);

            // 5. 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", String.valueOf(file.getSize()));

            return Result.success(result);

        } catch (IOException e) {
            log.error("文件上传 IO 异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("文件上传失败，请检查网络连接或稍后重试");
        } catch (IllegalArgumentException e) {
            log.error("文件上传参数异常: {}", e.getMessage(), e);
            return Result.error("上传参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传未知异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("文件上传失败，系统异常，请联系管理员");
        }
    }

    /**
     * 上传图片（专用接口，带图片格式校验）
     *
     * @param file 上传的图片文件
     * @return 上传结果
     */
    @PostMapping("/image")
    @RequireRole({Constant.ROLE_ADMIN, Constant.ROLE_TEACHER, Constant.ROLE_STUDENT})
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("接收到图片上传请求 - 文件名: {}", file.getOriginalFilename());

        try {
            // 1. 参数校验
            if (file.isEmpty()) {
                log.warn("上传图片为空");
                return Result.error("图片不能为空");
            }

            // 2. 图片大小校验（限制5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                log.warn("图片大小超过限制: {} bytes", file.getSize());
                return Result.error("图片大小不能超过 5MB");
            }

            // 3. 图片格式校验
            String contentType = file.getContentType();
            if (contentType == null || !isImageType(contentType)) {
                log.warn("不支持的图片类型: {}", contentType);
                return Result.error("只支持 JPG、PNG、GIF、WEBP 格式的图片");
            }

            // 4. 生成带日期路径的文件名
            String fileName = qiNiuUtil.generateDatePathFileName(file.getOriginalFilename());

            // 5. 上传到七牛云
            String fileUrl = qiNiuUtil.uploadFile(file, fileName);
            log.info("图片上传成功: {}", fileUrl);

            // 6. 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", file.getOriginalFilename());

            return Result.success(result);

        } catch (IOException e) {
            log.error("图片上传 IO 异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("图片上传失败，请检查网络连接或稍后重试");
        } catch (IllegalArgumentException e) {
            log.error("图片上传参数异常: {}", e.getMessage(), e);
            return Result.error("上传参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("图片上传未知异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("图片上传失败，系统异常，请联系管理员");
        }
    }

    /**
     * 上传课程封面
     *
     * @param file 封面图片
     * @return 上传结果
     */
    @PostMapping("/course-cover")
    @RequireRole(Constant.ROLE_ADMIN)
    public Result<Map<String, String>> uploadCourseCover(@RequestParam("file") MultipartFile file) {
        log.info("接收到课程封面上传请求 - 文件名: {}", file.getOriginalFilename());

        try {
            // 1. 图片校验
            if (file.isEmpty()) {
                log.warn("上传的课程封面为空");
                return Result.error("封面图片不能为空");
            }

            if (file.getSize() > 2 * 1024 * 1024) { // 2MB
                log.warn("课程封面大小超过限制: {} bytes", file.getSize());
                return Result.error("封面图片不能超过 2MB");
            }

            String contentType = file.getContentType();
            if (contentType == null || !isImageType(contentType)) {
                log.warn("课程封面类型不支持: {}", contentType);
                return Result.error("封面只支持图片格式");
            }

            // 2. 生成文件名（课程封面专用目录）
            String fileName = "course/cover/" + qiNiuUtil.generateUniqueFileName(file.getOriginalFilename());

            // 3. 上传
            String fileUrl = qiNiuUtil.uploadFile(file, fileName);
            log.info("课程封面上传成功: {}", fileUrl);

            // 4. 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);

            return Result.success(result);

        } catch (IOException e) {
            log.error("课程封面上传 IO 异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("封面上传失败，请检查网络连接或稍后重试");
        } catch (IllegalArgumentException e) {
            log.error("课程封面上传参数异常: {}", e.getMessage(), e);
            return Result.error("上传参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("课程封面上传未知异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("封面上传失败，系统异常，请联系管理员");
        }
    }

    /**
     * 上传用户头像
     *
     * @param file 头像图片
     * @return 上传结果
     */
    @PostMapping("/avatar")
    @RequireRole({Constant.ROLE_ADMIN, Constant.ROLE_TEACHER, Constant.ROLE_STUDENT})
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        log.info("接收到头像上传请求 - 文件名: {}", file.getOriginalFilename());

        try {
            // 1. 图片校验
            if (file.isEmpty()) {
                log.warn("上传的头像为空");
                return Result.error("头像图片不能为空");
            }

            if (file.getSize() > 1 * 1024 * 1024) { // 1MB
                log.warn("头像大小超过限制: {} bytes", file.getSize());
                return Result.error("头像图片不能超过 1MB");
            }

            String contentType = file.getContentType();
            if (contentType == null || !isImageType(contentType)) {
                log.warn("头像类型不支持: {}", contentType);
                return Result.error("头像只支持图片格式");
            }

            // 2. 生成文件名（头像专用目录）
            String fileName = "user/avatar/" + qiNiuUtil.generateUniqueFileName(file.getOriginalFilename());

            // 3. 上传
            String fileUrl = qiNiuUtil.uploadFile(file, fileName);
            log.info("头像上传成功: {}", fileUrl);

            // 4. 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);

            return Result.success(result);

        } catch (IOException e) {
            log.error("头像上传 IO 异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("头像上传失败，请检查网络连接或稍后重试");
        } catch (IllegalArgumentException e) {
            log.error("头像上传参数异常: {}", e.getMessage(), e);
            return Result.error("上传参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("头像上传未知异常 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            return Result.error("头像上传失败，系统异常，请联系管理员");
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名（key）
     * @return 删除结果
     */
    @DeleteMapping
    @RequireRole(Constant.ROLE_ADMIN)
    public Result<Void> deleteFile(@RequestParam("fileName") String fileName) {
        log.info("接收到文件删除请求 - 文件名: {}", fileName);

        try {
            // 参数校验
            if (fileName == null || fileName.trim().isEmpty()) {
                log.warn("删除文件名为空");
                return Result.error("文件名不能为空");
            }

            // 删除文件
            boolean success = qiNiuUtil.deleteFile(fileName);

            if (success) {
                log.info("文件删除成功: {}", fileName);
                return Result.success("文件删除成功");
            } else {
                log.warn("文件删除失败: {}", fileName);
                return Result.error("文件删除失败，文件可能不存在");
            }
        } catch (IllegalArgumentException e) {
            log.error("文件删除参数异常: {}", e.getMessage(), e);
            return Result.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件删除异常 - 文件名: {}, 错误: {}", fileName, e.getMessage(), e);
            return Result.error("文件删除失败，系统异常，请联系管理员");
        }
    }

    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedFileType(String contentType) {
        return contentType.startsWith("image/") ||
               contentType.startsWith("video/") ||
               contentType.equals("application/pdf") ||
               contentType.equals("application/msword") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
               contentType.equals("application/vnd.ms-excel") ||
               contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    /**
     * 检查是否为图片类型
     */
    private boolean isImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp") ||
               contentType.equals("image/jpg");
    }
}

