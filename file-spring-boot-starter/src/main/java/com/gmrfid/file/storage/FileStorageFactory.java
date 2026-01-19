package com.gmrfid.file.storage;

import com.gmrfid.file.storage.config.StorageProperties;
import com.gmrfid.file.storage.impl.AliyunOssStorageStrategy;
import com.gmrfid.file.storage.impl.LocalStorageStrategy;
import com.gmrfid.file.storage.impl.MinioStorageStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FileStorageFactory {

    private final Map<String, FileStorageStrategy> strategyMap = new HashMap<>();
    private final StorageProperties properties;
    private final String defaultType;

    public FileStorageFactory(StorageProperties properties) {
        this.properties = properties;
        this.defaultType = properties.getType();
        initStrategies();
    }

    private void initStrategies() {
        registerLocalStrategy();

        switch (defaultType.toLowerCase()) {
            case "minio":
                registerMinioStrategy();
                break;
            case "aliyun-oss":
                registerAliyunOssStrategy();
                break;
            case "local":
            default:
                break;
        }

        log.info("File storage factory init complete, default type: {}, registered strategies: {}",
                defaultType, strategyMap.keySet());
    }

    private void registerLocalStrategy() {
        try {
            LocalStorageStrategy strategy = new LocalStorageStrategy(
                    properties.getLocal(),
                    properties.getLocal().getBaseUrl());
            strategyMap.put(LocalStorageStrategy.STORAGE_TYPE, strategy);
            log.info("Registered Local storage strategy");
        } catch (Exception e) {
            log.error("Failed to register Local storage strategy", e);
        }
    }

    private void registerMinioStrategy() {
        try {
            StorageProperties.MinioProperties minioProps = properties.getMinio();
            if (minioProps.getEndpoint() == null || minioProps.getEndpoint().isEmpty()) {
                log.warn("MinIO config incomplete, skipping registration");
                return;
            }
            MinioStorageStrategy strategy = new MinioStorageStrategy(minioProps);
            strategyMap.put(MinioStorageStrategy.STORAGE_TYPE, strategy);
            log.info("Registered MinIO storage strategy");
        } catch (Exception e) {
            log.error("Failed to register MinIO storage strategy", e);
        }
    }

    private void registerAliyunOssStrategy() {
        try {
            StorageProperties.AliyunOssProperties ossProps = properties.getAliyunOss();
            if (ossProps.getEndpoint() == null || ossProps.getEndpoint().isEmpty()) {
                log.warn("Aliyun OSS config incomplete, skipping registration");
                return;
            }
            AliyunOssStorageStrategy strategy = new AliyunOssStorageStrategy(ossProps);
            strategyMap.put(AliyunOssStorageStrategy.STORAGE_TYPE, strategy);
            log.info("Registered Aliyun OSS storage strategy");
        } catch (Exception e) {
            log.error("Failed to register Aliyun OSS storage strategy", e);
        }
    }

    public FileStorageStrategy getStrategy() {
        return getStrategy(defaultType);
    }

    public FileStorageStrategy getStrategy(String storageType) {
        FileStorageStrategy strategy = strategyMap.get(storageType.toLowerCase());
        if (strategy == null) {
            strategy = strategyMap.get(defaultType.toLowerCase());
        }
        if (strategy == null) {
            strategy = strategyMap.get(LocalStorageStrategy.STORAGE_TYPE);
        }
        if (strategy == null) {
            throw new FileStorageException(500, "No available storage strategy found for: " + storageType);
        }
        return strategy;
    }

    public boolean isAvailable(String storageType) {
        return strategyMap.containsKey(storageType.toLowerCase());
    }

    public String[] getAvailableTypes() {
        return strategyMap.keySet().toArray(new String[0]);
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void registerStrategy(FileStorageStrategy strategy) {
        strategyMap.put(strategy.getStorageType(), strategy);
        log.info("Dynamically registered storage strategy: {}", strategy.getStorageType());
    }
}
