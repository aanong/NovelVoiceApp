package com.app.novelvoice.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.app.novelvoice.excel.config.ExcelColumnConfig;
import com.app.novelvoice.excel.config.ExcelSheetConfig;
import com.app.novelvoice.excel.dto.ExcelImportResult;
import com.app.novelvoice.excel.validator.ExcelExpressionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 动态Excel读取监听器
 * 根据YML配置动态读取和验证Excel数据
 */
@Slf4j
public class DynamicExcelListener extends AnalysisEventListener<Map<Integer, String>> {
    
    // Sheet配置
    private final ExcelSheetConfig sheetConfig;
    
    // 表达式验证器
    private final ExcelExpressionValidator expressionValidator;
    
    // 导入结果
    private final ExcelImportResult<Map<String, Object>> result;
    
    // 表头映射: 列索引 -> 列配置
    private final Map<Integer, ExcelColumnConfig> headerMapping = new HashMap<>();
    
    // 表头标题映射: 列索引 -> 标题
    private final Map<Integer, String> headerTitles = new HashMap<>();
    
    // 当前行号
    private int currentRow = 0;
    
    // 批处理大小
    private static final int BATCH_SIZE = 1000;
    
    // 批处理数据
    private final List<Map<String, Object>> batchData = new ArrayList<>();
    
    // 批处理回调
    private final BatchCallback batchCallback;
    
    public DynamicExcelListener(ExcelSheetConfig sheetConfig, 
                                 ExcelExpressionValidator expressionValidator,
                                 BatchCallback batchCallback) {
        this.sheetConfig = sheetConfig;
        this.expressionValidator = expressionValidator;
        this.batchCallback = batchCallback;
        this.result = new ExcelImportResult<>();
        this.result.setSuccess(true);
    }
    
    /**
     * 解析表头
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.debug("解析表头: {}", headMap);
        
        // 构建标题到配置的映射
        Map<String, ExcelColumnConfig> titleToConfig = new HashMap<>();
        if (sheetConfig.getColumns() != null) {
            for (ExcelColumnConfig column : sheetConfig.getColumns()) {
                titleToConfig.put(column.getTitle(), column);
            }
        }
        
        // 解析表头,建立列索引到配置的映射
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            Integer colIndex = entry.getKey();
            String title = entry.getValue().getStringValue();
            headerTitles.put(colIndex, title);
            
            ExcelColumnConfig config = titleToConfig.get(title);
            if (config != null) {
                headerMapping.put(colIndex, config);
            }
        }
    }
    
    /**
     * 每行数据解析回调
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        currentRow = context.readRowHolder().getRowIndex() + 1;
        result.setTotalRows(result.getTotalRows() + 1);
        
        // 检查最大行数限制
        if (sheetConfig.getMaxRowLimit() != null && 
                result.getTotalRows() > sheetConfig.getMaxRowLimit()) {
            log.warn("超过最大行数限制: {}", sheetConfig.getMaxRowLimit());
            return;
        }
        
        // 转换数据并验证
        Map<String, Object> rowData = new LinkedHashMap<>();
        List<String> rowErrors = new ArrayList<>();
        
        // 遍历配置的列
        for (Map.Entry<Integer, ExcelColumnConfig> entry : headerMapping.entrySet()) {
            Integer colIndex = entry.getKey();
            ExcelColumnConfig columnConfig = entry.getValue();
            String cellValue = data.get(colIndex);
            
            // 类型转换
            Object convertedValue = convertValue(cellValue, columnConfig);
            rowData.put(columnConfig.getField(), convertedValue);
            
            // 验证
            if (StringUtils.hasText(columnConfig.getVerifyExpression())) {
                boolean valid = expressionValidator.validate(cellValue, columnConfig.getVerifyExpression());
                if (!valid) {
                    String errorMsg = StringUtils.hasText(columnConfig.getErrorMessage()) 
                            ? columnConfig.getErrorMessage()
                            : String.format("字段[%s]验证失败", columnConfig.getTitle());
                    rowErrors.add(errorMsg);
                }
            }
            
            // 必填验证
            if (columnConfig.isRequired() && !StringUtils.hasText(cellValue)) {
                rowErrors.add(String.format("字段[%s]为必填项", columnConfig.getTitle()));
            }
        }
        
        // 处理验证结果
        if (rowErrors.isEmpty()) {
            result.setSuccessRows(result.getSuccessRows() + 1);
            result.getSuccessData().add(rowData);
            batchData.add(rowData);
            
            // 批处理
            if (batchData.size() >= BATCH_SIZE && batchCallback != null) {
                batchCallback.process(new ArrayList<>(batchData));
                batchData.clear();
            }
        } else {
            // 记录错误
            String combinedError = String.join("; ", rowErrors);
            result.addError(currentRow, null, null, combinedError, rowData);
            result.setSuccess(false);
        }
    }
    
    /**
     * 解析完成回调
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成, 总行数: {}, 成功: {}, 失败: {}", 
                result.getTotalRows(), result.getSuccessRows(), result.getFailedRows());
        
        // 处理剩余批次数据
        if (!batchData.isEmpty() && batchCallback != null) {
            batchCallback.process(new ArrayList<>(batchData));
            batchData.clear();
        }
        
        // 检查最小行数限制
        if (sheetConfig.getMinRowLimit() != null && 
                result.getTotalRows() < sheetConfig.getMinRowLimit()) {
            result.setSuccess(false);
            result.setMessage(String.format("数据行数少于最小限制: %d", sheetConfig.getMinRowLimit()));
        }
    }
    
    /**
     * 异常处理
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Excel解析异常, 行号: {}", currentRow, exception);
        result.setSuccess(false);
        result.setMessage("Excel解析异常: " + exception.getMessage());
    }
    
    /**
     * 获取导入结果
     */
    public ExcelImportResult<Map<String, Object>> getResult() {
        return result;
    }
    
    /**
     * 值类型转换
     */
    private Object convertValue(String value, ExcelColumnConfig config) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        
        String fieldType = config.getFieldType();
        if (fieldType == null) {
            fieldType = "string";
        }
        
        try {
            switch (fieldType.toLowerCase()) {
                case "integer":
                case "int":
                    return Integer.parseInt(value.trim());
                case "long":
                    return Long.parseLong(value.trim());
                case "double":
                case "decimal":
                    return Double.parseDouble(value.trim());
                case "boolean":
                case "bool":
                    return "是".equals(value.trim()) || "true".equalsIgnoreCase(value.trim()) 
                            || "1".equals(value.trim());
                case "date":
                    // 日期保持字符串格式,由业务层处理
                    return value.trim();
                default:
                    return value.trim();
            }
        } catch (Exception e) {
            log.warn("值转换失败: {} -> {}, 错误: {}", value, fieldType, e.getMessage());
            return value;
        }
    }
    
    /**
     * 批处理回调接口
     */
    @FunctionalInterface
    public interface BatchCallback {
        void process(List<Map<String, Object>> data);
    }
}
