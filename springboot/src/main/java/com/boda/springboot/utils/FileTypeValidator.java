package com.boda.springboot.utils;

import com.boda.springboot.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件类型校验工具类
 * 提供统一的文件类型校验功能
 */
@Slf4j
public class FileTypeValidator {

    /**
     * 允许上传的图片 MIME 类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
    );

    /**
     * 允许上传的图片扩展名
     */
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    /**
     * 校验图片类型（严格模式）
     * 同时校验扩展名和 MIME 类型
     *
     * @param file 上传的图片文件
     * @throws ServiceException 如果文件类型不符合要求
     */
    public static void validateImageType(MultipartFile file) {
        validateImageType(file, true);
    }

    /**
     * 校验图片类型
     *
     * @param file       上传的图片文件
     * @param strictMode 是否启用严格模式（同时校验 MIME 类型）
     * @throws ServiceException 如果文件类型不符合要求
     */
    public static void validateImageType(MultipartFile file, boolean strictMode) {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        log.info("校验图片类型 - 文件名: {}, MIME类型: {}, 严格模式: {}",
                originalFilename, contentType, strictMode);

        // 1. 校验文件扩展名
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ServiceException("文件名无效，必须包含扩展名");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new ServiceException("不支持的图片格式: " + extension +
                    "，仅支持: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }

        // 2. 校验 MIME 类型（可选）
        if (strictMode) {
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                log.warn("图片MIME类型校验失败 - 文件名: {}, MIME: {}", originalFilename, contentType);
                throw new ServiceException("不支持的图片MIME类型，仅支持常见图片格式");
            }
        }

        log.info("图片类型校验通过 - 扩展名: {}, MIME: {}", extension, contentType);
    }

    /**
     * 校验文件类型（通用方法）
     *
     * @param file              上传的文件
     * @param allowedExtensions 允许的扩展名列表
     * @param allowedTypes      允许的 MIME 类型列表
     * @throws ServiceException 如果文件类型不符合要求
     */
    public static void validateFileType(MultipartFile file,
                                        List<String> allowedExtensions,
                                        List<String> allowedTypes) {
        validateFileType(file, allowedExtensions, allowedTypes, true);
    }

    /**
     * 校验文件类型（通用方法）
     *
     * @param file              上传的文件
     * @param allowedExtensions 允许的扩展名列表
     * @param allowedTypes      允许的 MIME 类型列表
     * @param strictMode        是否启用严格模式（同时校验 MIME 类型）
     * @throws ServiceException 如果文件类型不符合要求
     */
    public static void validateFileType(MultipartFile file,
                                        List<String> allowedExtensions,
                                        List<String> allowedTypes,
                                        boolean strictMode) {
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        log.info("校验文件类型 - 文件名: {}, MIME类型: {}", originalFilename, contentType);

        // 1. 校验文件扩展名
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ServiceException("文件名无效，必须包含扩展名");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new ServiceException("不支持的文件类型: " + extension +
                    "，仅支持: " + String.join(", ", allowedExtensions));
        }

        // 2. 校验 MIME 类型
        if (strictMode) {
            if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
                log.warn("文件MIME类型校验失败 - 文件名: {}, MIME: {}", originalFilename, contentType);
                throw new ServiceException("不支持的文件MIME类型");
            }
        } else {
            // 非严格模式，只记录警告
            if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
                log.warn("文件MIME类型校验失败 - 文件名: {}, MIME: {}", originalFilename, contentType);
            }
        }

        log.info("文件类型校验通过 - 扩展名: {}, MIME: {}", extension, contentType);
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（小写），如果没有扩展名返回空字符串
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查是否为图片文件
     *
     * @param contentType MIME 类型
     * @return 是否为图片
     */
    public static boolean isImageType(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("image/");
    }

    /**
     * 检查是否为视频文件
     *
     * @param contentType MIME 类型
     * @return 是否为视频
     */
    public static boolean isVideoType(String contentType) {
        return contentType != null && contentType.toLowerCase().startsWith("video/");
    }

    /**
     * 获取允许的图片扩展名列表
     *
     * @return 扩展名列表
     */
    public static List<String> getAllowedImageExtensions() {
        return ALLOWED_IMAGE_EXTENSIONS;
    }

    /**
     * 获取允许的图片 MIME 类型列表
     *
     * @return MIME 类型列表
     */
    public static List<String> getAllowedImageTypes() {
        return ALLOWED_IMAGE_TYPES;
    }
}

