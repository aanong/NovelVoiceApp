package com.app.novelvoice.controller;

import com.app.novelvoice.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize; // 默认 10MB

    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(400, "只支持图片文件");
        }
        return saveFile(file, "images");
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload/file")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        // 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException(400, "文件大小超过限制");
        }
        return saveFile(file, "files");
    }

    /**
     * 保存文件到本地
     */
    private Map<String, Object> saveFile(MultipartFile file, String subDir) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 创建目录
            Path dirPath = Paths.get(uploadPath, subDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件
            Path filePath = dirPath.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", originalFilename);
            result.put("fileUrl", "/uploads/" + subDir + "/" + newFilename);
            result.put("fileSize", file.getSize());
            return result;
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }
}
