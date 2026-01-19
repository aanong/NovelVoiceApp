package com.gmrfid.file.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    /**
     * Current storage type: local, minio, aliyun-oss
     */
    private String type = "local";

    /**
     * Max file size in bytes, default 10MB
     */
    private long maxFileSize = 10485760;

    /**
     * Allowed content types
     */
    private String[] allowedContentTypes;

    private LocalProperties local = new LocalProperties();
    private MinioProperties minio = new MinioProperties();
    private AliyunOssProperties aliyunOss = new AliyunOssProperties();

    @Data
    public static class LocalProperties {
        private String path = "./uploads";
        private String baseUrl = "";
    }

    @Data
    public static class MinioProperties {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucket = "novelvoice";
        private boolean secure = false;
    }

    @Data
    public static class AliyunOssProperties {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucket;
        private String customDomain;
    }
}
