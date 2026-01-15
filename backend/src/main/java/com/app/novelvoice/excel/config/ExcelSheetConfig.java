package com.app.novelvoice.excel.config;

import lombok.Data;

import java.util.List;

/**
 * Excel Sheet配置
 * 用于定义单个Sheet的配置信息
 */
@Data
public class ExcelSheetConfig {
    
    /**
     * Sheet名称
     */
    private String name;
    
    /**
     * Sheet索引(从0开始)
     */
    private Integer sheetIndex = 0;
    
    /**
     * 最小行数限制
     */
    private Integer minRowLimit = 1;
    
    /**
     * 最大行数限制
     */
    private Integer maxRowLimit = 10000;
    
    /**
     * 表头行数(从第几行开始是数据)
     */
    private Integer headRowNumber = 1;
    
    /**
     * ORM映射类全限定名
     */
    private String ormClass;
    
    /**
     * 列配置列表
     */
    private List<ExcelColumnConfig> columns;
}
