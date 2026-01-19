package com.gmrfid.file.storage;

import lombok.Builder;
import lombok.Data;

/**
 * File Storage Result
 */
@Data
@Builder
public class FileStorageResult {

    private String originalFileName;
    private String storedFileName;
    private String fileUrl;
    private String fileKey;
    private Long fileSize;
    private String contentType;
    private String storageType;
    private String bucketName;
}
