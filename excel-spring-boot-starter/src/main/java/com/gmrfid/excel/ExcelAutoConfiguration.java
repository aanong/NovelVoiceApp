package com.gmrfid.excel;

import com.gmrfid.excel.config.ExcelConfigProperties;
import com.gmrfid.excel.converter.ExcelObjectConverter;
import com.gmrfid.excel.service.ExcelService;
import com.gmrfid.excel.validator.ExcelExpressionValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Excel功能自动配置类
 * 启用Excel相关的配置属性和组件扫描
 */
@Configuration
@EnableConfigurationProperties(ExcelConfigProperties.class)
@ComponentScan(basePackages = "com.gmrfid.excel")
public class ExcelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExcelExpressionValidator excelExpressionValidator() {
        return new ExcelExpressionValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcelObjectConverter excelObjectConverter() {
        return new ExcelObjectConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcelService excelService() {
        return new ExcelService();
    }
}
