package com.app.novelvoice.storage.impl;

import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.storage.FileStorageResult;
import com.app.novelvoice.storage.FileStorageStrategy;
import com.app.novelvoice.storage.config.StorageProperties;
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

/**
 * 本地文件存储策略实现
 * 将文件存储在服务器本地磁盘
 */
@Slf4j
public class LocalStorageStrategy implements FileStorageStrategy {

    /**
     * 存储类型标识
     */
    public static final String STORAGE_TYPE = "local";

    /**
     * 存储配置
     */
    private final StorageProperties.LocalProperties properties;

    /**
     * 文件访问基础URL
     */
    private final String baseUrl;

    public LocalStorageStrategy(StorageProperties.LocalProperties properties, String baseUrl) {
        this.properties = properties;
        this.baseUrl = baseUrl;
        // 初始化时创建上传目录
        initUploadDirectory();
    }

    /**
     * 初始化上传目录
     */
    private void initUploadDirectory() {
        try {
            Path uploadPath = Paths.get(properties.getPath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建本地存储目录: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建本地存储目录失败", e);
            throw new BusinessException(500, "初始化存储目录失败: " + e.getMessage());
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
            // 生成唯一文件名，保留原扩展名
            String extension = extractFileExtension(fileName);
            String storedFileName = UUID.randomUUID().toString() + extension;

            // 构建存储路径
            Path dirPath = Paths.get(properties.getPath(), subDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件
            Path filePath = dirPath.resolve(storedFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // 构建文件Key和访问URL
            String fileKey = subDir + "/" + storedFileName;
            String fileUrl = baseUrl + "/uploads/" + fileKey;

            log.info("文件上传成功 [本地存储]: {} -> {}", fileName, filePath.toAbsolutePath());

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
            log.error("本地文件存储失败", e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileKey) {
        try {
            Path filePath = Paths.get(properties.getPath(), fileKey);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功 [本地存储]: {}", fileKey);
                return true;
            }
            log.warn("文件不存在，无法删除 [本地存储]: {}", fileKey);
            return false;
        } catch (IOException e) {
            log.error("删除本地文件失败: {}", fileKey, e);
            throw new BusinessException(500, "文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        return baseUrl + "/uploads/" + fileKey;
    }

    @Override
    public String getPresignedUrl(String fileKey, int expireSeconds) {
        // 本地存储不支持预签名URL，直接返回普通URL
        return getFileUrl(fileKey);
    }

    @Override
    public boolean exists(String fileKey) {
        Path filePath = Paths.get(properties.getPath(), fileKey);
        return Files.exists(filePath);
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
