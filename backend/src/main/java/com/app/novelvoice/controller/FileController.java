package com.app.novelvoice.controller;

import com.app.novelvoice.storage.FileStorageResult;
import com.app.novelvoice.storage.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 使用策略模式支持多种存储方式（本地、MinIO、阿里云OSS）
 * 文件直接存储在对象存储中，不存储到数据库
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    /**
     * 文件存储服务
     */
    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 上传图片
     * 仅接受图片类型文件
     * @param file 图片文件
     * @return 上传结果
     */
    @PostMapping("/upload/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        FileStorageResult result = storageService.uploadImage(file);
        return buildResponse(result);
    }

    /**
     * 上传文件
     * 接受任意类型文件
     * @param file 文件
     * @return 上传结果
     */
    @PostMapping("/upload/file")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        FileStorageResult result = storageService.uploadFile(file);
        return buildResponse(result);
    }

    /**
     * 上传文件到指定子目录
     * @param file 文件
     * @param subDir 子目录名称（如：avatars、covers、chat-images等）
     * @return 上传结果
     */
    @PostMapping("/upload/{subDir}")
    public Map<String, Object> uploadToDir(@RequestParam("file") MultipartFile file,
                                           @PathVariable("subDir") String subDir) {
        FileStorageResult result = storageService.upload(file, subDir);
        return buildResponse(result);
    }

    /**
     * 上传文件（通用接口）
     * 支持指定存储类型和子目录
     * @param file 文件
     * @param subDir 子目录（默认files）
     * @param storageType 存储类型（local/minio/aliyun-oss，可选）
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "subDir", defaultValue = "files") String subDir,
                                      @RequestParam(value = "storageType", required = false) String storageType) {
        FileStorageResult result;
        if (storageType != null && !storageType.isEmpty()) {
            result = storageService.upload(file, subDir, storageType);
        } else {
            result = storageService.upload(file, subDir);
        }
        return buildResponse(result);
    }

    /**
     * 删除文件
     * @param fileKey 文件Key（存储路径）
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteFile(@RequestParam("fileKey") String fileKey) {
        boolean success = storageService.delete(fileKey);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("fileKey", fileKey);
        return response;
    }

    /**
     * 获取文件预签名URL
     * 用于临时访问私有文件
     * @param fileKey 文件Key
     * @param expireSeconds 过期时间（秒），默认3600秒
     * @return 预签名URL
     */
    @GetMapping("/presigned-url")
    public Map<String, Object> getPresignedUrl(@RequestParam("fileKey") String fileKey,
                                               @RequestParam(value = "expireSeconds", defaultValue = "3600") int expireSeconds) {
        String url = storageService.getPresignedUrl(fileKey, expireSeconds);
        Map<String, Object> response = new HashMap<>();
        response.put("url", url);
        response.put("fileKey", fileKey);
        response.put("expireSeconds", expireSeconds);
        return response;
    }

    /**
     * 检查文件是否存在
     * @param fileKey 文件Key
     * @return 是否存在
     */
    @GetMapping("/exists")
    public Map<String, Object> checkExists(@RequestParam("fileKey") String fileKey) {
        boolean exists = storageService.exists(fileKey);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("fileKey", fileKey);
        return response;
    }

    /**
     * 获取存储信息
     * @return 当前存储类型和可用存储类型
     */
    @GetMapping("/storage-info")
    public Map<String, Object> getStorageInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("currentType", storageService.getCurrentStorageType());
        response.put("availableTypes", storageService.getAvailableStorageTypes());
        return response;
    }

    /**
     * 构建响应结果
     * @param result 存储结果
     * @return 响应Map
     */
    private Map<String, Object> buildResponse(FileStorageResult result) {
        Map<String, Object> response = new HashMap<>();
        response.put("fileName", result.getOriginalFileName());
        response.put("storedFileName", result.getStoredFileName());
        response.put("fileUrl", result.getFileUrl());
        response.put("fileKey", result.getFileKey());
        response.put("fileSize", result.getFileSize());
        response.put("contentType", result.getContentType());
        response.put("storageType", result.getStorageType());
        if (result.getBucketName() != null) {
            response.put("bucketName", result.getBucketName());
        }
        return response;
    }
}
