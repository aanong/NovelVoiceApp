package com.app.novelvoice.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储策略接口
 * 使用策略模式，定义统一的文件存储操作接口
 * 支持本地存储、MinIO、阿里云OSS等多种存储方式
 */
public interface FileStorageStrategy {

    /**
     * 获取存储类型标识
     * @return 存储类型名称（如：local、minio、aliyun-oss）
     */
    String getStorageType();

    /**
     * 上传文件
     * @param file 上传的文件
     * @param subDir 子目录（如：images、files）
     * @return 文件存储结果
     */
    FileStorageResult upload(MultipartFile file, String subDir);

    /**
     * 上传文件（通过输入流）
     * @param inputStream 文件输入流
     * @param fileName 文件名
     * @param contentType 文件类型
     * @param fileSize 文件大小
     * @param subDir 子目录
     * @return 文件存储结果
     */
    FileStorageResult upload(InputStream inputStream, String fileName, String contentType, 
                             long fileSize, String subDir);

    /**
     * 删除文件
     * @param fileKey 文件标识（路径或key）
     * @return 是否删除成功
     */
    boolean delete(String fileKey);

    /**
     * 获取文件访问URL
     * @param fileKey 文件标识
     * @return 文件访问URL
     */
    String getFileUrl(String fileKey);

    /**
     * 获取文件预签名URL（临时访问链接）
     * @param fileKey 文件标识
     * @param expireSeconds 过期时间（秒）
     * @return 预签名URL
     */
    String getPresignedUrl(String fileKey, int expireSeconds);

    /**
     * 检查文件是否存在
     * @param fileKey 文件标识
     * @return 是否存在
     */
    boolean exists(String fileKey);
}
