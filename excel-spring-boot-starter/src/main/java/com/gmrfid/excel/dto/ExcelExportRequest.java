package com.gmrfid.excel.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * Excel导出请求封?
 */
@Data
public class ExcelExportRequest {

    /**
     * 任务类型(必须匹配yml配置)
     */
    private String taskType;

    /**
     * 导出文件名(可选,默认使用任务名)
     */
    private String fileName;

    /**
     * Sheet索引(默认0)
     */
    private Integer sheetIndex = 0;

    /**
     * 待导出的数据列表(Map形式)
     */
    private List<Map<String, Object>> mapData;

    /**
     * 待导出的数据列表(Java对象形式)
     */
    private List<?> data;

    /**
     * 需要包含的字段(可选)
     */
    private List<String> includeFields;

    /**
     * 需要排除的字段(可选)
     */
    private List<String> excludeFields;
}
