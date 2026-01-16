package com.app.tool.excel.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Excel导出请求
 */
@Data
public class ExcelExportRequest {

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * Sheet索引(默认0)
     */
    private Integer sheetIndex = 0;

    /**
     * 导出文件名
     */
    private String fileName;

    /**
     * 导出数据(实体列表)
     */
    private List<?> data;

    /**
     * 导出数据(Map列表)
     */
    private List<Map<String, Object>> mapData;

    /**
     * 是否包含表头
     */
    private boolean includeHeader = true;

    /**
     * 只导出指定字段(为空则导出所有)
     */
    private List<String> includeFields;

    /**
     * 排除指定字段
     */
    private List<String> excludeFields;
}
