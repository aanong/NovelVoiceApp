package com.app.novelvoice.storage.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.storage.FileStorageResult;
import com.app.novelvoice.storage.FileStorageStrategy;
import com.app.novelvoice.storage.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云 OSS 对象存储策略实现
 * 支持阿里云 OSS 对象存储服务
 */
@Slf4j
public class AliyunOssStorageStrategy implements FileStorageStrategy {

    /**
     * 存储类型标识
     */
    public static final String STORAGE_TYPE = "aliyun-oss";

    /**
     * 阿里云 OSS 客户端
     */
    private final OSS ossClient;

    /**
     * 存储配置
     */
    private final StorageProperties.AliyunOssProperties properties;

    public AliyunOssStorageStrategy(StorageProperties.AliyunOssProperties properties) {
        this.properties = properties;
        // 初始化 OSS 客户端
        this.ossClient = new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret()
        );
        // 确保存储桶存在
        ensureBucketExists();
    }

    /**
     * 确保存储桶存在，不存在则创建
     */
    private void ensureBucketExists() {
        try {
            if (!ossClient.doesBucketExist(properties.getBucket())) {
                ossClient.createBucket(properties.getBucket());
                log.info("创建阿里云 OSS 存储桶: {}", properties.getBucket());
            }
        } catch (Exception e) {
            log.error("检查或创建阿里云 OSS 存储桶失败", e);
            throw new BusinessException(500, "阿里云 OSS 存储桶初始化失败: " + e.getMessage());
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

            // 设置对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(contentType);

            // 创建上传请求
            PutObjectRequest putRequest = new PutObjectRequest(
                    properties.getBucket(),
                    objectKey,
                    inputStream,
                    metadata
            );

            // 上传到阿里云 OSS
            ossClient.putObject(putRequest);

            // 构建访问URL
            String fileUrl = buildFileUrl(objectKey);

            log.info("文件上传成功 [阿里云OSS]: {} -> {}/{}", fileName, properties.getBucket(), objectKey);

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
            log.error("阿里云 OSS 文件上传失败", e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            ossClient.deleteObject(properties.getBucket(), fileKey);
            log.info("文件删除成功 [阿里云OSS]: {}/{}", properties.getBucket(), fileKey);
            return true;
        } catch (Exception e) {
            log.error("删除阿里云 OSS 文件失败: {}", fileKey, e);
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
            // 计算过期时间
            Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);
            
            // 生成预签名URL请求
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    properties.getBucket(), 
                    fileKey
            );
            request.setExpiration(expiration);

            // 生成预签名URL
            URL signedUrl = ossClient.generatePresignedUrl(request);
            return signedUrl.toString();
        } catch (Exception e) {
            log.error("获取阿里云 OSS 预签名URL失败: {}", fileKey, e);
            throw new BusinessException(500, "获取预签名URL失败: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String fileKey) {
        try {
            return ossClient.doesObjectExist(properties.getBucket(), fileKey);
        } catch (Exception e) {
            log.error("检查阿里云 OSS 文件是否存在失败: {}", fileKey, e);
            return false;
        }
    }

    /**
     * 构建文件访问URL
     * 如果配置了自定义域名，则使用自定义域名
     * @param objectKey 对象Key
     * @return 访问URL
     */
    private String buildFileUrl(String objectKey) {
        // 如果配置了自定义域名
        if (properties.getCustomDomain() != null && !properties.getCustomDomain().isEmpty()) {
            String domain = properties.getCustomDomain();
            if (!domain.startsWith("http")) {
                domain = "https://" + domain;
            }
            if (domain.endsWith("/")) {
                domain = domain.substring(0, domain.length() - 1);
            }
            return domain + "/" + objectKey;
        }
        
        // 使用默认的 Bucket 域名
        String endpoint = properties.getEndpoint();
        // 移除 https:// 或 http:// 前缀
        String pureEndpoint = endpoint.replaceFirst("^https?://", "");
        return "https://" + properties.getBucket() + "." + pureEndpoint + "/" + objectKey;
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

    /**
     * 销毁时关闭 OSS 客户端
     */
    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("阿里云 OSS 客户端已关闭");
        }
    }
}
