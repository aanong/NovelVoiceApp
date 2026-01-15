package com.app.novelvoice.storage.config;

import com.app.novelvoice.storage.FileStorageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储自动配置类
 * 负责初始化文件存储相关的Bean
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageAutoConfiguration {

    /**
     * 创建文件存储工厂Bean
     * @param properties 存储配置属性
     * @return 文件存储工厂实例
     */
    @Bean
    public FileStorageFactory fileStorageFactory(StorageProperties properties) {
        log.info("初始化文件存储工厂，存储类型: {}", properties.getType());
        return new FileStorageFactory(properties);
    }
}
