package com.gmrfid.file.storage.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.gmrfid.file.storage.FileStorageException;
import com.gmrfid.file.storage.FileStorageResult;
import com.gmrfid.file.storage.FileStorageStrategy;
import com.gmrfid.file.storage.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class AliyunOssStorageStrategy implements FileStorageStrategy {

    public static final String STORAGE_TYPE = "aliyun-oss";
    private final OSS ossClient;
    private final StorageProperties.AliyunOssProperties properties;

    public AliyunOssStorageStrategy(StorageProperties.AliyunOssProperties properties) {
        this.properties = properties;
        this.ossClient = new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret());
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            if (!ossClient.doesBucketExist(properties.getBucket())) {
                ossClient.createBucket(properties.getBucket());
                log.info("Created Aliyun OSS bucket: {}", properties.getBucket());
            }
        } catch (Exception e) {
            log.error("Failed to check or create Aliyun OSS bucket", e);
            throw new FileStorageException(500, "Aliyun OSS bucket init failed: " + e.getMessage());
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
            log.error("Failed to read upload file", e);
            throw new FileStorageException(500, "File upload failed: " + e.getMessage());
        }
    }

    @Override
    public FileStorageResult upload(InputStream inputStream, String fileName, String contentType,
            long fileSize, String subDir) {
        try {
            String extension = extractFileExtension(fileName);
            String storedFileName = UUID.randomUUID().toString() + extension;
            String objectKey = subDir + "/" + storedFileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(contentType);

            PutObjectRequest putRequest = new PutObjectRequest(
                    properties.getBucket(),
                    objectKey,
                    inputStream,
                    metadata);

            ossClient.putObject(putRequest);

            String fileUrl = buildFileUrl(objectKey);
            log.info("File uploaded successfully [Aliyun OSS]: {} -> {}/{}", fileName, properties.getBucket(),
                    objectKey);

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
            log.error("Aliyun OSS file upload failed", e);
            throw new FileStorageException(500, "File upload failed: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            ossClient.deleteObject(properties.getBucket(), fileKey);
            log.info("File deleted successfully [Aliyun OSS]: {}/{}", properties.getBucket(), fileKey);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete Aliyun OSS file: {}", fileKey, e);
            throw new FileStorageException(500, "File delete failed: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        return buildFileUrl(fileKey);
    }

    @Override
    public String getPresignedUrl(String fileKey, int expireSeconds) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    properties.getBucket(),
                    fileKey);
            request.setExpiration(expiration);
            URL signedUrl = ossClient.generatePresignedUrl(request);
            return signedUrl.toString();
        } catch (Exception e) {
            log.error("Failed to get Aliyun OSS presigned URL: {}", fileKey, e);
            throw new FileStorageException(500, "Failed to get presigned URL: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String fileKey) {
        try {
            return ossClient.doesObjectExist(properties.getBucket(), fileKey);
        } catch (Exception e) {
            log.error("Failed to check Aliyun OSS existence: {}", fileKey, e);
            return false;
        }
    }

    private String buildFileUrl(String objectKey) {
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

        String endpoint = properties.getEndpoint();
        String pureEndpoint = endpoint.replaceFirst("^https?://", "");
        return "https://" + properties.getBucket() + "." + pureEndpoint + "/" + objectKey;
    }

    private String extractFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("Aliyun OSS client shutdown");
        }
    }
}
