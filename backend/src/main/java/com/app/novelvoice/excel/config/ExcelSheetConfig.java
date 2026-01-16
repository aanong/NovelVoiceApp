package com.app.novelvoice.excel.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Excel Sheet配置
 * 用于定义单个Sheet的配置信息
 */
@Data
@Slf4j
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
     * 缓存的ORM类对象
     */
    private transient Class<?> ormClassType;

    /**
     * 列配置列表
     */
    private List<ExcelColumnConfig> columns;

    /**
     * 获取ORM映射类对象
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
            log.warn("无法加载ORM类: {}", ormClass, e);
            return null;
        }
    }

    /**
     * 检查是否配置了有效的ORM类
     */
    public boolean hasOrmClass() {
        return getOrmClassType() != null;
    }
}
