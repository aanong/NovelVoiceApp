package com.app.novelvoice.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置属性类
 * 支持本地存储、MinIO、阿里云OSS等多种存储方式的配置
 */
@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /**
     * 当前使用的存储类型
     * 可选值: local, minio, aliyun-oss
     */
    private String type = "local";

    /**
     * 允许的最大文件大小（字节），默认 10MB
     */
    private long maxFileSize = 10485760;

    /**
     * 允许的文件类型（MIME类型），为空表示不限制
     */
    private String[] allowedContentTypes;

    /**
     * 本地存储配置
     */
    private LocalProperties local = new LocalProperties();

    /**
     * MinIO 存储配置
     */
    private MinioProperties minio = new MinioProperties();

    /**
     * 阿里云 OSS 存储配置
     */
    private AliyunOssProperties aliyunOss = new AliyunOssProperties();

    /**
     * 本地存储配置属性
     */
    @Data
    public static class LocalProperties {
        /**
         * 本地存储路径
         */
        private String path = "./uploads";

        /**
         * 文件访问基础URL
         */
        private String baseUrl = "";
    }

    /**
     * MinIO 存储配置属性
     */
    @Data
    public static class MinioProperties {
        /**
         * MinIO 服务端点
         * 例如: http://localhost:9000
         */
        private String endpoint;

        /**
         * 访问密钥
         */
        private String accessKey;

        /**
         * 秘密密钥
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucket = "novelvoice";

        /**
         * 是否使用 HTTPS
         */
        private boolean secure = false;
    }

    /**
     * 阿里云 OSS 存储配置属性
     */
    @Data
    public static class AliyunOssProperties {
        /**
         * OSS 服务端点
         * 例如: oss-cn-hangzhou.aliyuncs.com
         */
        private String endpoint;

        /**
         * 访问密钥 ID
         */
        private String accessKeyId;

        /**
         * 访问密钥 Secret
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucket;

        /**
         * 自定义域名（可选）
         */
        private String customDomain;
    }
}
