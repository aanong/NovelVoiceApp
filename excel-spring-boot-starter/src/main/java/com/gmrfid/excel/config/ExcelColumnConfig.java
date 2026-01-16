package com.gmrfid.excel.config;

import lombok.Data;

import java.util.List;

/**
 * Excel列配�?
 * 用于定义导入导出时的列映射关系和验证规则
 */
@Data
public class ExcelColumnConfig {

    /**
     * 列标�?表头显示名称)
     */
    private String title;

    /**
     * 对应的实体字段名
     */
    private String field;

    /**
     * 字段类型: string, integer, long, double, date, boolean
     */
    private String fieldType = "string";

    /**
     * 日期格式(当fieldType为date时使�?
     */
    private String dateFormat = "yyyy-MM-dd";

    /**
     * 验证表达�?SpEL表达�?
     * 支持: #notBlank(#val), #empty(#val), #lengthLessThan(#val, 64),
     * #options(#val, 'opt1', 'opt2'), #dateFormat(#val, 'yyyy/MM/dd'),
     * #doubleWithScale(#val, 2), #longGreaterThan(#val, 0) �?
     */
    private String verifyExpression;

    /**
     * 验证失败时的错误提示信息
     */
    private String errorMessage;

    /**
     * 是否必填
     */
    private boolean required = false;

    /**
     * 列宽(导出时使�?
     */
    private Integer width = 20;

    /**
     * 列索�?可�?不指定则按顺�?
     */
    private Integer index;

    /**
     * 是否导出(默认导出)
     */
    private boolean exportable = true;

    /**
     * 是否导入(默认导入)
     */
    private boolean importable = true;

    /**
     * 字典转换器名�?用于枚举值转�?
     */
    private String converter;
}
