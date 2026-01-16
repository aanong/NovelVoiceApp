package com.app.tool.excel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel导入结果
 * 
 * @param <T> 成功数据的类型
 */
@Data
public class ExcelImportResult<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 总行数
     */
    private int totalRows;

    /**
     * 成功行数
     */
    private int successRows;

    /**
     * 失败行数
     */
    private int failedRows;

    /**
     * 成功数据列表
     */
    private List<T> successData = new ArrayList<>();

    /**
     * 失败数据列表(包含行号和错误信息)
     */
    private List<RowError> errors = new ArrayList<>();

    /**
     * 原始数据(Map形式,用于导出失败数据)
     */
    private List<Map<String, Object>> failedRawData = new ArrayList<>();

    /**
     * 错误信息
     */
    private String message;

    /**
     * 行错误信息
     */
    @Data
    public static class RowError {
        /**
         * 行号(从1开始,含表头)
         */
        private int rowIndex;

        /**
         * 错误字段
         */
        private String field;

        /**
         * 错误值
         */
        private Object value;

        /**
         * 错误信息
         */
        private String errorMessage;

        /**
         * 原始行数据
         */
        private Map<String, Object> rowData;

        public RowError() {
        }

        public RowError(int rowIndex, String field, Object value, String errorMessage) {
            this.rowIndex = rowIndex;
            this.field = field;
            this.value = value;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * 创建成功结果
     */
    public static <T> ExcelImportResult<T> success(List<T> data) {
        ExcelImportResult<T> result = new ExcelImportResult<>();
        result.setSuccess(true);
        result.setTotalRows(data.size());
        result.setSuccessRows(data.size());
        result.setFailedRows(0);
        result.setSuccessData(data);
        result.setMessage("导入成功");
        return result;
    }

    /**
     * 创建失败结果
     */
    public static <T> ExcelImportResult<T> fail(String message) {
        ExcelImportResult<T> result = new ExcelImportResult<>();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    /**
     * 添加错误
     */
    public void addError(int rowIndex, String field, Object value, String errorMessage,
            Map<String, Object> rowData) {
        RowError error = new RowError(rowIndex, field, value, errorMessage);
        error.setRowData(rowData);
        this.errors.add(error);
        if (rowData != null) {
            // 添加错误信息到原始数据
            rowData.put("_errorMessage", errorMessage);
            this.failedRawData.add(rowData);
        }
        this.failedRows++;
    }
}
