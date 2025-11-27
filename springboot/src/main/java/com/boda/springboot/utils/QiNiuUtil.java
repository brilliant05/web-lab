package com.boda.springboot.utils;

import com.boda.springboot.properties.QiNiuProperties;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 七牛云对象存储工具类
 */
@Slf4j
@Component
public class QiNiuUtil {

    @Autowired
    private QiNiuProperties qiNiuProperties;

    /**
     * 上传文件到七牛云
     *
     * @param file 上传的文件
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 1. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 2. 上传文件
        return uploadFile(file.getInputStream(), fileName);
    }

    /**
     * 上传文件到七牛云（自定义文件名）
     *
     * @param file 上传的文件
     * @param fileName 自定义文件名
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        return uploadFile(file.getInputStream(), fileName);
    }

    /**
     * 上传文件流到七牛云
     *
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadFile(InputStream inputStream, String fileName) throws IOException {
        // 1. 构造配置对象（根据区域选择）
        Configuration cfg = new Configuration(getRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

        // 2. 创建上传管理器
        UploadManager uploadManager = new UploadManager(cfg);

        // 3. 生成上传凭证
        Auth auth = Auth.create(qiNiuProperties.getAccessKey(), qiNiuProperties.getSecretKey());
        String upToken = auth.uploadToken(qiNiuProperties.getBucket());

        try {
            // 4. 执行上传
            Response response = uploadManager.put(inputStream, fileName, upToken, null, null);

            // 5. 解析上传结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            // 6. 返回文件访问URL
            String fileUrl = qiNiuProperties.getDomain() + "/" + putRet.key;
            log.info("文件上传成功: {}", fileUrl);

            return fileUrl;

        } catch (QiniuException e) {
            log.error("七牛云上传失败: {}", e.response.toString());
            throw new IOException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组到七牛云
     *
     * @param bytes 字节数组
     * @param fileName 文件名
     * @return 文件访问URL
     * @throws IOException IO异常
     */
    public String uploadBytes(byte[] bytes, String fileName) throws IOException {
        // 构造配置对象
        Configuration cfg = new Configuration(getRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

        // 创建上传管理器
        UploadManager uploadManager = new UploadManager(cfg);

        // 生成上传凭证
        Auth auth = Auth.create(qiNiuProperties.getAccessKey(), qiNiuProperties.getSecretKey());
        String upToken = auth.uploadToken(qiNiuProperties.getBucket());

        try {
            // 执行上传
            Response response = uploadManager.put(bytes, fileName, upToken);

            // 解析上传结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            // 返回文件访问URL
            String fileUrl = qiNiuProperties.getDomain() + "/" + putRet.key;
            log.info("文件上传成功: {}", fileUrl);

            return fileUrl;

        } catch (QiniuException e) {
            log.error("七牛云上传失败: {}", e.response.toString());
            throw new IOException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除七牛云中的文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileName) {
        // 构造配置对象
        Configuration cfg = new Configuration(getRegion());

        // 创建认证对象
        Auth auth = Auth.create(qiNiuProperties.getAccessKey(), qiNiuProperties.getSecretKey());

        // 创建存储管理器
        com.qiniu.storage.BucketManager bucketManager = new com.qiniu.storage.BucketManager(auth, cfg);

        try {
            // 执行删除
            bucketManager.delete(qiNiuProperties.getBucket(), fileName);
            log.info("文件删除成功: {}", fileName);
            return true;
        } catch (QiniuException e) {
            log.error("文件删除失败: {}", e.response.toString());
            return false;
        }
    }

    /**
     * 根据配置的区域代码获取 Region 对象
     *
     * @return Region 对象
     */
    private Region getRegion() {
        String zone = qiNiuProperties.getZone();
        return switch (zone) {
            case "z0" -> Region.huadong();      // 华东
            case "z1" -> Region.huabei();       // 华北
            case "z2" -> Region.huanan();       // 华南
            case "na0" -> Region.beimei();      // 北美
            case "as0" -> Region.xinjiapo();    // 新加坡
            default -> Region.autoRegion();     // 自动判断
        };
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 生成带日期路径的文件名
     *
     * @param originalFilename 原始文件名
     * @return 带日期路径的文件名（例如：2024/11/27/uuid.jpg）
     */
    public String generateDatePathFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 获取当前日期
        java.time.LocalDate now = java.time.LocalDate.now();
        String datePath = now.getYear() + "/" +
                         String.format("%02d", now.getMonthValue()) + "/" +
                         String.format("%02d", now.getDayOfMonth());

        return datePath + "/" + UUID.randomUUID().toString().replace("-", "") + extension;
    }
}

