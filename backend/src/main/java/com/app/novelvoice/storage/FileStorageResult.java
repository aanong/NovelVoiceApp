package com.app.novelvoice.storage;

import lombok.Builder;
import lombok.Data;

/**
 * 文件存储结果
 * 封装文件上传后的返回信息
 */
@Data
@Builder
public class FileStorageResult {

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 存储后的文件名
     */
    private String storedFileName;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件存储路径/Key
     */
    private String fileKey;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 存储类型（local/minio/aliyun-oss）
     */
    private String storageType;

    /**
     * 存储桶名称（对象存储使用）
     */
    private String bucketName;
}
