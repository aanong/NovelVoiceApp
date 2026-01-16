package com.gmrfid.excel.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel Sheet配置
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
    private int minRowLimit = 1;

    /**
     * 最大行数限制
     */
    private int maxRowLimit = 10000;

    /**
     * 表头行数
     */
    private int headRowNumber = 1;

    /**
     * ORM映射类全限定名
     */
    private String ormClass;

    /**
     * 缓存加载的类对象
     */
    private transient Class<?> ormClassType;

    /**
     * 列配置列表
     */
    private List<ExcelColumnConfig> columns = new ArrayList<>();

    /**
     * 获取ORM类对象
     */
    public Class<?> getOrmClassType() {
        if (ormClassType == null && ormClass != null && !ormClass.isEmpty()) {
            try {
                ormClassType = ClassUtils.forName(ormClass, null);
            } catch (ClassNotFoundException e) {
                log.error("未找到配置的ormClass: {}", ormClass);
            }
        }
        return ormClassType;
    }

    /**
     * 是否包含有效的ORM类配置
     */
    public boolean hasOrmClass() {
        return getOrmClassType() != null;
    }
}
