package com.gmrfid.file.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface FileStorageStrategy {

    String getStorageType();

    FileStorageResult upload(MultipartFile file, String subDir);

    FileStorageResult upload(InputStream inputStream, String fileName, String contentType,
            long fileSize, String subDir);

    boolean delete(String fileKey);

    String getFileUrl(String fileKey);

    String getPresignedUrl(String fileKey, int expireSeconds);

    boolean exists(String fileKey);
}
