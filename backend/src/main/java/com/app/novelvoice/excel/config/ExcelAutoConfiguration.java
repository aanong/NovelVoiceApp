package com.app.novelvoice.excel.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Excel功能自动配置类
 * 启用Excel相关的配置属性和组件扫描
 */
@Configuration
@EnableConfigurationProperties(ExcelConfigProperties.class)
@ComponentScan(basePackages = "com.app.novelvoice.excel")
public class ExcelAutoConfiguration {
    // 自动配置类,无需额外代码
}
