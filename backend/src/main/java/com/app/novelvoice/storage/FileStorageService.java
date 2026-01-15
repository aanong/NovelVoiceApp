package com.app.novelvoice.storage;

import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.storage.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件存储服务
 * 对外提供统一的文件存储操作接口
 * 内部使用策略模式委托给具体的存储策略实现
 */
@Slf4j
@Service
public class FileStorageService {

    /**
     * 存储策略工厂
     */
    private final FileStorageFactory storageFactory;

    /**
     * 存储配置
     */
    private final StorageProperties properties;

    /**
     * 允许的内容类型集合
     */
    private final Set<String> allowedContentTypes;

    /**
     * 图片类型前缀
     */
    private static final String IMAGE_TYPE_PREFIX = "image/";

    /**
     * 默认子目录
     */
    private static final String DEFAULT_SUB_DIR = "files";

    /**
     * 图片子目录
     */
    private static final String IMAGE_SUB_DIR = "images";

    public FileStorageService(FileStorageFactory storageFactory, StorageProperties properties) {
        this.storageFactory = storageFactory;
        this.properties = properties;
        // 初始化允许的内容类型
        this.allowedContentTypes = new HashSet<>();
        if (properties.getAllowedContentTypes() != null) {
            allowedContentTypes.addAll(Arrays.asList(properties.getAllowedContentTypes()));
        }
    }

    /**
     * 上传图片文件
     * @param file 图片文件
     * @return 存储结果
     */
    public FileStorageResult uploadImage(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(IMAGE_TYPE_PREFIX)) {
            throw new BusinessException(400, "只支持图片文件，当前类型: " + contentType);
        }
        return upload(file, IMAGE_SUB_DIR);
    }

    /**
     * 上传普通文件
     * @param file 文件
     * @return 存储结果
     */
    public FileStorageResult uploadFile(MultipartFile file) {
        return upload(file, DEFAULT_SUB_DIR);
    }

    /**
     * 上传文件到指定子目录
     * @param file 文件
     * @param subDir 子目录
     * @return 存储结果
     */
    public FileStorageResult upload(MultipartFile file, String subDir) {
        // 验证文件
        validateFile(file);

        // 获取默认存储策略并上传
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.upload(file, subDir);
    }

    /**
     * 上传文件到指定存储类型
     * @param file 文件
     * @param subDir 子目录
     * @param storageType 存储类型
     * @return 存储结果
     */
    public FileStorageResult upload(MultipartFile file, String subDir, String storageType) {
        // 验证文件
        validateFile(file);

        // 获取指定类型的存储策略并上传
        FileStorageStrategy strategy = storageFactory.getStrategy(storageType);
        return strategy.upload(file, subDir);
    }

    /**
     * 通过输入流上传文件
     * @param inputStream 输入流
     * @param fileName 文件名
     * @param contentType 内容类型
     * @param fileSize 文件大小
     * @param subDir 子目录
     * @return 存储结果
     */
    public FileStorageResult upload(InputStream inputStream, String fileName, 
                                    String contentType, long fileSize, String subDir) {
        // 验证文件大小
        if (fileSize > properties.getMaxFileSize()) {
            throw new BusinessException(400, "文件大小超过限制，最大允许: " + 
                    formatFileSize(properties.getMaxFileSize()));
        }

        // 获取默认存储策略并上传
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.upload(inputStream, fileName, contentType, fileSize, subDir);
    }

    /**
     * 删除文件
     * @param fileKey 文件标识
     * @return 是否删除成功
     */
    public boolean delete(String fileKey) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.delete(fileKey);
    }

    /**
     * 删除指定存储类型的文件
     * @param fileKey 文件标识
     * @param storageType 存储类型
     * @return 是否删除成功
     */
    public boolean delete(String fileKey, String storageType) {
        FileStorageStrategy strategy = storageFactory.getStrategy(storageType);
        return strategy.delete(fileKey);
    }

    /**
     * 获取文件访问URL
     * @param fileKey 文件标识
     * @return 访问URL
     */
    public String getFileUrl(String fileKey) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.getFileUrl(fileKey);
    }

    /**
     * 获取文件预签名URL
     * @param fileKey 文件标识
     * @param expireSeconds 过期时间（秒）
     * @return 预签名URL
     */
    public String getPresignedUrl(String fileKey, int expireSeconds) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.getPresignedUrl(fileKey, expireSeconds);
    }

    /**
     * 检查文件是否存在
     * @param fileKey 文件标识
     * @return 是否存在
     */
    public boolean exists(String fileKey) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.exists(fileKey);
    }

    /**
     * 获取当前使用的存储类型
     * @return 存储类型标识
     */
    public String getCurrentStorageType() {
        return storageFactory.getDefaultType();
    }

    /**
     * 获取所有可用的存储类型
     * @return 存储类型数组
     */
    public String[] getAvailableStorageTypes() {
        return storageFactory.getAvailableTypes();
    }

    /**
     * 验证上传文件
     * @param file 文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > properties.getMaxFileSize()) {
            throw new BusinessException(400, "文件大小超过限制，最大允许: " + 
                    formatFileSize(properties.getMaxFileSize()));
        }

        // 检查文件类型（如果配置了类型限制）
        if (!allowedContentTypes.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !allowedContentTypes.contains(contentType)) {
                throw new BusinessException(400, "不支持的文件类型: " + contentType);
            }
        }
    }

    /**
     * 格式化文件大小
     * @param size 文件大小（字节）
     * @return 格式化后的大小字符串
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
