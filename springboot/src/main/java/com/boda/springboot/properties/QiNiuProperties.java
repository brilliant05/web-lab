package com.boda.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 七牛云配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiNiuProperties {

    /**
     * AccessKey（从七牛云控制台获取）
     */
    private String accessKey;

    /**
     * SecretKey（从七牛云控制台获取）
     */
    private String secretKey;

    /**
     * 存储空间名称（Bucket）
     */
    private String bucket;

    /**
     * CDN 加速域名（用于访问上传的文件）
     */
    private String domain;

    /**
     * 存储区域
     * 华东: z0, 华北: z1, 华南: z2, 北美: na0, 东南亚: as0
     */
    private String zone = "z0";
}

