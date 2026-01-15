package com.app.novelvoice.storage;

import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.storage.config.StorageProperties;
import com.app.novelvoice.storage.impl.AliyunOssStorageStrategy;
import com.app.novelvoice.storage.impl.LocalStorageStrategy;
import com.app.novelvoice.storage.impl.MinioStorageStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件存储策略工厂
 * 使用工厂模式创建和管理不同的存储策略实例
 * 支持动态获取指定类型的存储策略
 */
@Slf4j
public class FileStorageFactory {

    /**
     * 存储策略缓存
     * Key: 存储类型标识
     * Value: 存储策略实例
     */
    private final Map<String, FileStorageStrategy> strategyMap = new HashMap<>();

    /**
     * 存储配置
     */
    private final StorageProperties properties;

    /**
     * 默认存储类型
     */
    private final String defaultType;

    public FileStorageFactory(StorageProperties properties) {
        this.properties = properties;
        this.defaultType = properties.getType();
        // 初始化时注册所有可用的存储策略
        initStrategies();
    }

    /**
     * 初始化存储策略
     * 根据配置注册对应的存储策略实例
     */
    private void initStrategies() {
        // 始终注册本地存储策略作为后备方案
        registerLocalStrategy();

        // 根据配置的默认类型初始化对应策略
        switch (defaultType.toLowerCase()) {
            case "minio":
                registerMinioStrategy();
                break;
            case "aliyun-oss":
                registerAliyunOssStrategy();
                break;
            case "local":
            default:
                // 本地存储已经注册
                break;
        }

        log.info("文件存储工厂初始化完成，默认存储类型: {}, 已注册策略: {}", 
                defaultType, strategyMap.keySet());
    }

    /**
     * 注册本地存储策略
     */
    private void registerLocalStrategy() {
        try {
            LocalStorageStrategy strategy = new LocalStorageStrategy(
                    properties.getLocal(),
                    properties.getLocal().getBaseUrl()
            );
            strategyMap.put(LocalStorageStrategy.STORAGE_TYPE, strategy);
            log.info("注册本地存储策略成功");
        } catch (Exception e) {
            log.error("注册本地存储策略失败", e);
        }
    }

    /**
     * 注册 MinIO 存储策略
     */
    private void registerMinioStrategy() {
        try {
            StorageProperties.MinioProperties minioProps = properties.getMinio();
            if (minioProps.getEndpoint() == null || minioProps.getEndpoint().isEmpty()) {
                log.warn("MinIO 配置不完整，跳过注册");
                return;
            }
            MinioStorageStrategy strategy = new MinioStorageStrategy(minioProps);
            strategyMap.put(MinioStorageStrategy.STORAGE_TYPE, strategy);
            log.info("注册 MinIO 存储策略成功");
        } catch (Exception e) {
            log.error("注册 MinIO 存储策略失败", e);
        }
    }

    /**
     * 注册阿里云 OSS 存储策略
     */
    private void registerAliyunOssStrategy() {
        try {
            StorageProperties.AliyunOssProperties ossProps = properties.getAliyunOss();
            if (ossProps.getEndpoint() == null || ossProps.getEndpoint().isEmpty()) {
                log.warn("阿里云 OSS 配置不完整，跳过注册");
                return;
            }
            AliyunOssStorageStrategy strategy = new AliyunOssStorageStrategy(ossProps);
            strategyMap.put(AliyunOssStorageStrategy.STORAGE_TYPE, strategy);
            log.info("注册阿里云 OSS 存储策略成功");
        } catch (Exception e) {
            log.error("注册阿里云 OSS 存储策略失败", e);
        }
    }

    /**
     * 获取默认存储策略
     * @return 默认存储策略实例
     */
    public FileStorageStrategy getStrategy() {
        return getStrategy(defaultType);
    }

    /**
     * 根据类型获取存储策略
     * @param storageType 存储类型标识
     * @return 对应的存储策略实例
     */
    public FileStorageStrategy getStrategy(String storageType) {
        FileStorageStrategy strategy = strategyMap.get(storageType.toLowerCase());
        if (strategy == null) {
            // 如果请求的类型不存在，尝试使用默认类型
            strategy = strategyMap.get(defaultType.toLowerCase());
        }
        if (strategy == null) {
            // 最后尝试使用本地存储
            strategy = strategyMap.get(LocalStorageStrategy.STORAGE_TYPE);
        }
        if (strategy == null) {
            throw new BusinessException(500, "未找到可用的存储策略: " + storageType);
        }
        return strategy;
    }

    /**
     * 检查指定类型的存储策略是否可用
     * @param storageType 存储类型标识
     * @return 是否可用
     */
    public boolean isAvailable(String storageType) {
        return strategyMap.containsKey(storageType.toLowerCase());
    }

    /**
     * 获取所有已注册的存储类型
     * @return 存储类型列表
     */
    public String[] getAvailableTypes() {
        return strategyMap.keySet().toArray(new String[0]);
    }

    /**
     * 获取默认存储类型
     * @return 默认存储类型标识
     */
    public String getDefaultType() {
        return defaultType;
    }

    /**
     * 动态注册存储策略
     * @param strategy 存储策略实例
     */
    public void registerStrategy(FileStorageStrategy strategy) {
        strategyMap.put(strategy.getStorageType(), strategy);
        log.info("动态注册存储策略: {}", strategy.getStorageType());
    }
}
