package com.app.tool.excel;

import com.app.tool.excel.config.ExcelConfigProperties;
import com.app.tool.excel.converter.ExcelObjectConverter;
import com.app.tool.excel.service.ExcelService;
import com.app.tool.excel.validator.ExcelExpressionValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Excel功能自动配置类
 * 启用Excel相关的配置属性和组件扫描
 * 
 * 使用方式:
 * 1. 在项目中引入此starter依赖
 * 2. 在application.yml中配置excel任务
 * 3. 注入ExcelService使用
 */
@Configuration
@EnableConfigurationProperties(ExcelConfigProperties.class)
@ComponentScan(basePackages = "com.app.tool.excel")
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
