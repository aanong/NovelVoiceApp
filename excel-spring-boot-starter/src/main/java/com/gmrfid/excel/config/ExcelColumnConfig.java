package com.gmrfid.excel.config;

import lombok.Data;

/**
 * Excel列配置
 */
@Data
public class ExcelColumnConfig {

    /**
     * 列标题
     */
    private String title;

    /**
     * 对应的实体字段名
     */
    private String field;

    /**
     * 字段类型 (string, int, long, double, date, boolean)
     */
    private String fieldType = "string";

    /**
     * 日期格式 (如果是日期类型)
     */
    private String dateFormat = "yyyy-MM-dd";

    /**
     * 验证表达式 (SpEL)
     */
    private String verifyExpression;

    /**
     * 验证失败时的错误提示
     */
    private String errorMessage;

    /**
     * 是否必填
     */
    private boolean required = false;

    /**
     * 列宽
     */
    private int width = 20;

    /**
     * 是否可导出
     */
    private boolean exportable = true;

    /**
     * 是否可导入
     */
    private boolean importable = true;
}
