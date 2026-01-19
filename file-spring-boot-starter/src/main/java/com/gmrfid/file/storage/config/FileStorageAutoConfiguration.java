package com.gmrfid.file.storage.config;

import com.gmrfid.file.storage.FileStorageFactory;
import com.gmrfid.file.storage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class FileStorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FileStorageFactory fileStorageFactory(StorageProperties properties) {
        log.info("Init FileStorageFactory, type: {}", properties.getType());
        return new FileStorageFactory(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(FileStorageFactory fileStorageFactory, StorageProperties properties) {
        log.info("Init FileStorageService");
        return new FileStorageService(fileStorageFactory, properties);
    }
}
