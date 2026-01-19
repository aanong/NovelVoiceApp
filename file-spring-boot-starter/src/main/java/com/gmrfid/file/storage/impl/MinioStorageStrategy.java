package com.gmrfid.file.storage.impl;

import com.gmrfid.file.storage.FileStorageException;
import com.gmrfid.file.storage.FileStorageResult;
import com.gmrfid.file.storage.FileStorageStrategy;
import com.gmrfid.file.storage.config.StorageProperties;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MinioStorageStrategy implements FileStorageStrategy {

    public static final String STORAGE_TYPE = "minio";
    private final MinioClient minioClient;
    private final StorageProperties.MinioProperties properties;

    public MinioStorageStrategy(StorageProperties.MinioProperties properties) {
        this.properties = properties;
        this.minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build());
                log.info("Created MinIO bucket: {}", properties.getBucket());
            }
        } catch (Exception e) {
            log.error("Failed to check or create MinIO bucket", e);
            throw new FileStorageException(500, "MinIO bucket init failed: " + e.getMessage());
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

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectKey)
                            .stream(inputStream, fileSize, -1)
                            .contentType(contentType)
                            .build());

            String fileUrl = buildFileUrl(objectKey);
            log.info("File uploaded successfully [MinIO]: {} -> {}/{}", fileName, properties.getBucket(), objectKey);

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
            log.error("MinIO file upload failed", e);
            throw new FileStorageException(500, "File upload failed: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .build());
            log.info("File deleted successfully [MinIO]: {}/{}", properties.getBucket(), fileKey);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete MinIO file: {}", fileKey, e);
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
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .method(Method.GET)
                            .expiry(expireSeconds, TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            log.error("Failed to get MinIO presigned URL: {}", fileKey, e);
            throw new FileStorageException(500, "Failed to get presigned URL: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String fileKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(fileKey)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String buildFileUrl(String objectKey) {
        String endpoint = properties.getEndpoint();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + properties.getBucket() + "/" + objectKey;
    }

    private String extractFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
