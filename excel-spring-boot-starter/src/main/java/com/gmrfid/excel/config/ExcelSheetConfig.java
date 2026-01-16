package com.gmrfid.excel.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Excel Sheet配置
 * 用于定义单个Sheet的配置信�?
 */
@Data
@Slf4j
public class ExcelSheetConfig {

    /**
     * Sheet名称
     */
    private String name;

    /**
     * Sheet索引(�?开�?
     */
    private Integer sheetIndex = 0;

    /**
     * 最小行数限�?
     */
    private Integer minRowLimit = 1;

    /**
     * 最大行数限�?
     */
    private Integer maxRowLimit = 10000;

    /**
     * 表头行数(从第几行开始是数据)
     */
    private Integer headRowNumber = 1;

    /**
     * ORM映射类全限定�?
     */
    private String ormClass;

    /**
     * 缓存的ORM类对�?
     */
    private transient Class<?> ormClassType;

    /**
     * 列配置列�?
     */
    private List<ExcelColumnConfig> columns;

    /**
     * 获取ORM映射类对�?
     * 
     * @return ORM类对象，如果配置无效或类不存在则返回null
     */
    public Class<?> getOrmClassType() {
        if (ormClassType != null) {
            return ormClassType;
        }
        if (ormClass == null || ormClass.isEmpty()) {
            return null;
        }
        try {
            ormClassType = Class.forName(ormClass);
            return ormClassType;
        } catch (ClassNotFoundException e) {
            log.warn("无法加载ORM�? {}", ormClass, e);
            return null;
        }
    }

    /**
     * 检查是否配置了有效的ORM�?
     */
    public boolean hasOrmClass() {
        return getOrmClassType() != null;
    }
}
