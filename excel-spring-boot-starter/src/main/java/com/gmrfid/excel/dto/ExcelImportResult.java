package com.gmrfid.excel.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel导入结果封装
 * 
 * @param <T> 成功数据的类型(Map或指定的Java对象)
 */
@Data
public class ExcelImportResult<T> {

    /**
     * 是否全部成?
     */
    private boolean success = true;

    /**
     * 总行?(不含表头)
     */
    private int totalRows = 0;

    /**
     * 成功行数
     */
    private int successRows = 0;

    /**
     * 失败行数
     */
    private int failedRows = 0;

    /**
     * 成功的数据列?
     */
    private List<T> successData = new ArrayList<>();

    /**
     * 失败的原始数据列表(Map形式,用于导出失败数据)
     */
    private List<Map<String, Object>> failedRawData = new ArrayList<>();

    /**
     * 错误信息列表
     */
    private List<String> errorMessages = new ArrayList<>();

    /**
     * 静态构造: 失败
     */
    public static <T> ExcelImportResult<T> fail(String message) {
        ExcelImportResult<T> result = new ExcelImportResult<>();
        result.setSuccess(false);
        result.getErrorMessages().add(message);
        return result;
    }

    /**
     * 添加成功记录
     */
    public void addSuccess(T data) {
        this.successRows++;
        this.successData.add(data);
    }

    /**
     * 添加失败记录
     */
    public void addFailed(Map<String, Object> rawData, String message) {
        this.success = false;
        this.failedRows++;
        this.failedRawData.add(rawData);
        this.errorMessages.add("第" + (totalRows + 1) + "行: " + message);
    }

    /**
     * 增加总行?
     */
    public void incrementTotal() {
        this.totalRows++;
    }
}
