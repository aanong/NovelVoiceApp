package com.gmrfid.file.storage.impl;

import com.gmrfid.file.storage.FileStorageException;
import com.gmrfid.file.storage.FileStorageResult;
import com.gmrfid.file.storage.FileStorageStrategy;
import com.gmrfid.file.storage.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
public class LocalStorageStrategy implements FileStorageStrategy {

    public static final String STORAGE_TYPE = "local";
    private final StorageProperties.LocalProperties properties;
    private final String baseUrl;

    public LocalStorageStrategy(StorageProperties.LocalProperties properties, String baseUrl) {
        this.properties = properties;
        this.baseUrl = baseUrl;
        initUploadDirectory();
    }

    private void initUploadDirectory() {
        try {
            Path uploadPath = Paths.get(properties.getPath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created local storage directory: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create local storage directory", e);
            throw new FileStorageException(500, "Failed to init storage directory: " + e.getMessage());
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
            Path dirPath = Paths.get(properties.getPath(), subDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path filePath = dirPath.resolve(storedFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileKey = subDir + "/" + storedFileName;
            String fileUrl = baseUrl + "/uploads/" + fileKey;
            log.info("File uploaded successfully [Local]: {} -> {}", fileName, filePath.toAbsolutePath());
            return FileStorageResult.builder()
                    .originalFileName(fileName)
                    .storedFileName(storedFileName)
                    .fileUrl(fileUrl)
                    .fileKey(fileKey)
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .storageType(STORAGE_TYPE)
                    .build();
        } catch (IOException e) {
            log.error("Local file storage failed", e);
            throw new FileStorageException(500, "File upload failed: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            Path filePath = Paths.get(properties.getPath(), fileKey);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("File deleted successfully [Local]: {}", fileKey);
                return true;
            }
            log.warn("File not found, cannot delete [Local]: {}", fileKey);
            return false;
        } catch (IOException e) {
            log.error("Failed to delete local file: {}", fileKey, e);
            throw new FileStorageException(500, "File delete failed: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        return baseUrl + "/uploads/" + fileKey;
    }

    @Override
    public String getPresignedUrl(String fileKey, int expireSeconds) {
        return getFileUrl(fileKey);
    }

    @Override
    public boolean exists(String fileKey) {
        Path filePath = Paths.get(properties.getPath(), fileKey);
        return Files.exists(filePath);
    }

    private String extractFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
