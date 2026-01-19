package com.gmrfid.file.storage;

import com.gmrfid.file.storage.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class FileStorageService {

    private final FileStorageFactory storageFactory;
    private final StorageProperties properties;
    private final Set<String> allowedContentTypes;
    private static final String IMAGE_TYPE_PREFIX = "image/";
    private static final String DEFAULT_SUB_DIR = "files";
    private static final String IMAGE_SUB_DIR = "images";

    public FileStorageService(FileStorageFactory storageFactory, StorageProperties properties) {
        this.storageFactory = storageFactory;
        this.properties = properties;
        this.allowedContentTypes = new HashSet<>();
        if (properties.getAllowedContentTypes() != null) {
            allowedContentTypes.addAll(Arrays.asList(properties.getAllowedContentTypes()));
        }
    }

    public FileStorageResult uploadImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(IMAGE_TYPE_PREFIX)) {
            throw new FileStorageException(400, "Only image files are supported, current type: " + contentType);
        }
        return upload(file, IMAGE_SUB_DIR);
    }

    public FileStorageResult uploadFile(MultipartFile file) {
        return upload(file, DEFAULT_SUB_DIR);
    }

    public FileStorageResult upload(MultipartFile file, String subDir) {
        validateFile(file);
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.upload(file, subDir);
    }

    public FileStorageResult upload(MultipartFile file, String subDir, String storageType) {
        validateFile(file);
        FileStorageStrategy strategy = storageFactory.getStrategy(storageType);
        return strategy.upload(file, subDir);
    }

    public FileStorageResult upload(InputStream inputStream, String fileName,
            String contentType, long fileSize, String subDir) {
        if (fileSize > properties.getMaxFileSize()) {
            throw new FileStorageException(400, "File size exceeds limit, max allowed: " +
                    formatFileSize(properties.getMaxFileSize()));
        }
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.upload(inputStream, fileName, contentType, fileSize, subDir);
    }

    public boolean delete(String fileKey) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.delete(fileKey);
    }

    public boolean delete(String fileKey, String storageType) {
        FileStorageStrategy strategy = storageFactory.getStrategy(storageType);
        return strategy.delete(fileKey);
    }

    public String getFileUrl(String fileKey) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.getFileUrl(fileKey);
    }

    public String getPresignedUrl(String fileKey, int expireSeconds) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.getPresignedUrl(fileKey, expireSeconds);
    }

    public boolean exists(String fileKey) {
        FileStorageStrategy strategy = storageFactory.getStrategy();
        return strategy.exists(fileKey);
    }

    public String getCurrentStorageType() {
        return storageFactory.getDefaultType();
    }

    public String[] getAvailableStorageTypes() {
        return storageFactory.getAvailableTypes();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException(400, "Upload file cannot be empty");
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new FileStorageException(400, "File size exceeds limit, max allowed: " +
                    formatFileSize(properties.getMaxFileSize()));
        }

        if (!allowedContentTypes.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !allowedContentTypes.contains(contentType)) {
                throw new FileStorageException(400, "Unsupported file type: " + contentType);
            }
        }
    }

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
