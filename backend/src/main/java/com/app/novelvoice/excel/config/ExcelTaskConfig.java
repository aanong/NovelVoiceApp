package com.app.novelvoice.excel.config;

import lombok.Data;

import java.util.List;

/**
 * Excel任务配置
 * 用于定义一个完整的导入/导出任务
 */
@Data
public class ExcelTaskConfig {
    
    /**
     * 任务类型(唯一标识)
     */
    private String type;
    
    /**
     * 任务名称(描述)
     */
    private String name;
    
    /**
     * 是否使用文件缓冲
     */
    private boolean uploadUseFileBuffer = false;
    
    /**
     * 失败文件描述
     */
    private String failureFileDescription = "下载失败数据";
    
    /**
     * 是否验证重复任务
     */
    private boolean verifyDuplicateTask = false;
    
    /**
     * 最大并行运行数
     */
    private Integer maxParallelRunNum = 10;
    
    /**
     * 导出时的文件名模板(支持日期占位符)
     */
    private String exportFileName;
    
    /**
     * Sheet配置列表
     */
    private List<ExcelSheetConfig> sheets;
}
