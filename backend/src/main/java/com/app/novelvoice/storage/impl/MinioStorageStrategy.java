package com.app.novelvoice.storage.impl;

import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.storage.FileStorageResult;
import com.app.novelvoice.storage.FileStorageStrategy;
import com.app.novelvoice.storage.config.StorageProperties;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 对象存储策略实现
 * 支持私有化部署的对象存储服务
 */
@Slf4j
public class MinioStorageStrategy implements FileStorageStrategy {

    /**
     * 存储类型标识
     */
    public static final String STORAGE_TYPE = "minio";

    /**
     * MinIO 客户端
     */
    private final MinioClient minioClient;

    /**
     * 存储配置
     */
    private final StorageProperties.MinioProperties properties;

    public MinioStorageStrategy(StorageProperties.MinioProperties properties) {
        this.properties = properties;
        // 初始化 MinIO 客户端
        this.minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        // 确保存储桶存在
        ensureBucketExists();
    }

    /**
     * 确保存储桶存在，不存在则创建
     */
    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build()
                );
                log.info("创建 MinIO 存储桶: {}", properties.getBucket());
            }
        } catch (Exception e) {
            log.error("检查或创建 MinIO 存储桶失败", e);
            throw new BusinessException(500, "MinIO 存储桶初始化失败: " + e.getMessage());
        }
    }

    @Override
    public String getStorageType() {
        return STORAGE_TYPE;
    }

    @Override
    public FileStorageResult upload(MultipartFile file, String subDir) {
        try {
            return upload(file.getInputStream(), file.getOriginalFilename(),
                    file.getContentType(), file.getSize(), subDir);
        } catch (IOException e) {
            log.error("读取上传文件失败", e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileStorageResult upload(InputStream inputStream, String fileName, String contentType,
                                    long fileSize, String subDir) {
        try {
            // 生成唯一文件名
            String extension = extractFileExtension(fileName);
            String storedFileName = UUID.randomUUID().toString() + extension;

            // 构建对象Key（路径）
            String objectKey = subDir + "/" + storedFileName;

            // 上传到 MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .stream(inputStream, fileSize, -1)
                            .contentType(contentType)
                            .build()
            );

            // 构建访问URL
            String fileUrl = buildFileUrl(objectKey);

            log.info("文件上传成功 [MinIO]: {} -> {}/{}", fileName, properties.getBucket(), objectKey);

            return FileStorageResult.builder()
                    .originalFileName(fileName)
                    .storedFileName(storedFileName)
                    .fileUrl(fileUrl)
                    .fileKey(objectKey)
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .storageType(STORAGE_TYPE)
                    .bucketName(properties.getBucket())
                    .build();
        } catch (Exception e) {
            log.error("MinIO 文件上传失败", e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .build()
            );
            log.info("文件删除成功 [MinIO]: {}/{}", properties.getBucket(), fileKey);
            return true;
        } catch (Exception e) {
            log.error("删除 MinIO 文件失败: {}", fileKey, e);
            throw new BusinessException(500, "文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        return buildFileUrl(fileKey);
    }

    @Override
    public String getPresignedUrl(String fileKey, int expireSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .method(Method.GET)
                            .expiry(expireSeconds, TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取 MinIO 预签名URL失败: {}", fileKey, e);
            throw new BusinessException(500, "获取预签名URL失败: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String fileKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 构建文件访问URL
     * @param objectKey 对象Key
     * @return 访问URL
     */
    private String buildFileUrl(String objectKey) {
        String endpoint = properties.getEndpoint();
        // 移除末尾斜杠
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + properties.getBucket() + "/" + objectKey;
    }

    /**
     * 提取文件扩展名
     * @param fileName 文件名
     * @return 扩展名（包含点号）
     */
    private String extractFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
