package com.gmrfid.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.gmrfid.excel.config.ExcelColumnConfig;
import com.gmrfid.excel.config.ExcelSheetConfig;
import com.gmrfid.excel.converter.ExcelObjectConverter;
import com.gmrfid.excel.dto.ExcelImportResult;
import com.gmrfid.excel.validator.ExcelExpressionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 动态Excel读取监听�?
 * 根据YML配置动态读取和验证Excel数据
 * 支持将数据转换为ormClass指定的Java对象
 */
@Slf4j
public class DynamicExcelListener extends AnalysisEventListener<Map<Integer, String>> {

    // Sheet配置
    private final ExcelSheetConfig sheetConfig;

    // 表达式验证器
    private final ExcelExpressionValidator expressionValidator;

    // 对象转换�?可�?
    private final ExcelObjectConverter objectConverter;

    // 导入结果(Map形式)
    private final ExcelImportResult<Map<String, Object>> mapResult;

    // 导入结果(类型化形�?
    private ExcelImportResult<Object> typedResult;

    // 表头映射: 列索�?-> 列配�?
    private final Map<Integer, ExcelColumnConfig> headerMapping = new HashMap<>();

    // 表头标题映射: 列索�?-> 标题
    private final Map<Integer, String> headerTitles = new HashMap<>();

    // 当前行号
    private int currentRow = 0;

    // 批处理大�?
    private static final int BATCH_SIZE = 1000;

    // 批处理数�?Map形式)
    private final List<Map<String, Object>> batchData = new ArrayList<>();

    // 批处理数�?类型化形�?
    private final List<Object> typedBatchData = new ArrayList<>();

    // 批处理回�?
    private final BatchCallback batchCallback;

    // 类型化批处理回调
    private final TypedBatchCallback<?> typedBatchCallback;

    // 是否启用类型化转�?
    private final boolean enableTypedConversion;

    /**
     * 构造函�?Map形式)
     */
    public DynamicExcelListener(ExcelSheetConfig sheetConfig,
            ExcelExpressionValidator expressionValidator,
            BatchCallback batchCallback) {
        this(sheetConfig, expressionValidator, null, batchCallback, null);
    }

    /**
     * 构造函�?支持类型化转�?
     */
    public DynamicExcelListener(ExcelSheetConfig sheetConfig,
            ExcelExpressionValidator expressionValidator,
            ExcelObjectConverter objectConverter,
            TypedBatchCallback<?> typedBatchCallback) {
        this(sheetConfig, expressionValidator, objectConverter, null, typedBatchCallback);
    }

    /**
     * 完整构造函�?
     */
    public DynamicExcelListener(ExcelSheetConfig sheetConfig,
            ExcelExpressionValidator expressionValidator,
            ExcelObjectConverter objectConverter,
            BatchCallback batchCallback,
            TypedBatchCallback<?> typedBatchCallback) {
        this.sheetConfig = sheetConfig;
        this.expressionValidator = expressionValidator;
        this.objectConverter = objectConverter;
        this.batchCallback = batchCallback;
        this.typedBatchCallback = typedBatchCallback;
        this.mapResult = new ExcelImportResult<>();
        this.mapResult.setSuccess(true);

        // 判断是否启用类型化转�?
        this.enableTypedConversion = objectConverter != null && sheetConfig.hasOrmClass();
        if (enableTypedConversion) {
            this.typedResult = new ExcelImportResult<>();
            this.typedResult.setSuccess(true);
        }
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

        // 解析表头,建立列索引到配置的映�?
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
        mapResult.setTotalRows(mapResult.getTotalRows() + 1);
        if (enableTypedConversion) {
            typedResult.setTotalRows(typedResult.getTotalRows() + 1);
        }

        // 检查最大行数限�?
        if (sheetConfig.getMaxRowLimit() != null &&
                mapResult.getTotalRows() > sheetConfig.getMaxRowLimit()) {
            log.warn("超过最大行数限�? {}", sheetConfig.getMaxRowLimit());
            return;
        }

        // 转换数据并验�?
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
            mapResult.setSuccessRows(mapResult.getSuccessRows() + 1);
            mapResult.getSuccessData().add(rowData);
            batchData.add(rowData);

            // 类型化转�?
            if (enableTypedConversion) {
                Object typedObj = objectConverter.convertToObject(rowData, sheetConfig);
                if (typedObj != null) {
                    typedResult.setSuccessRows(typedResult.getSuccessRows() + 1);
                    typedResult.getSuccessData().add(typedObj);
                    typedBatchData.add(typedObj);
                }
            }

            // 批处�?
            if (batchData.size() >= BATCH_SIZE) {
                if (batchCallback != null) {
                    batchCallback.process(new ArrayList<>(batchData));
                }
                if (typedBatchCallback != null && !typedBatchData.isEmpty()) {
                    processTypedBatch();
                }
                batchData.clear();
                typedBatchData.clear();
            }
        } else {
            // 记录错误
            String combinedError = String.join("; ", rowErrors);
            mapResult.addError(currentRow, null, null, combinedError, rowData);
            mapResult.setSuccess(false);
            if (enableTypedConversion) {
                typedResult.setSuccess(false);
            }
        }
    }

    /**
     * 解析完成回调
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel解析完成, 总行�? {}, 成功: {}, 失败: {}",
                mapResult.getTotalRows(), mapResult.getSuccessRows(), mapResult.getFailedRows());

        // 处理剩余批次数据
        if (!batchData.isEmpty()) {
            if (batchCallback != null) {
                batchCallback.process(new ArrayList<>(batchData));
            }
            if (typedBatchCallback != null && !typedBatchData.isEmpty()) {
                processTypedBatch();
            }
            batchData.clear();
            typedBatchData.clear();
        }

        // 检查最小行数限�?
        if (sheetConfig.getMinRowLimit() != null &&
                mapResult.getTotalRows() < sheetConfig.getMinRowLimit()) {
            mapResult.setSuccess(false);
            mapResult.setMessage(String.format("数据行数少于最小限�? %d", sheetConfig.getMinRowLimit()));
            if (enableTypedConversion) {
                typedResult.setSuccess(false);
                typedResult.setMessage(mapResult.getMessage());
            }
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("Excel解析异常, 行号: {}", currentRow, exception);
        mapResult.setSuccess(false);
        mapResult.setMessage("Excel解析异常: " + exception.getMessage());
        if (enableTypedConversion) {
            typedResult.setSuccess(false);
            typedResult.setMessage(mapResult.getMessage());
        }
    }

    /**
     * 获取导入结果(Map形式)
     */
    public ExcelImportResult<Map<String, Object>> getResult() {
        return mapResult;
    }

    /**
     * 获取导入结果(类型化形�?
     * 
     * @param <T> 目标类型
     * @return 类型化的导入结果
     */
    @SuppressWarnings("unchecked")
    public <T> ExcelImportResult<T> getTypedResult() {
        if (!enableTypedConversion) {
            log.warn("未启用类型化转换，请确保提供了有效的ObjectConverter和ormClass配置");
            return null;
        }
        return (ExcelImportResult<T>) typedResult;
    }

    /**
     * 处理类型化批次数�?
     */
    @SuppressWarnings("unchecked")
    private <T> void processTypedBatch() {
        if (typedBatchCallback != null) {
            ((TypedBatchCallback<T>) typedBatchCallback).process((List<T>) new ArrayList<>(typedBatchData));
        }
    }

    /**
     * 值类型转�?
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
                    return "�?.equals(value.trim()) || "true".equalsIgnoreCase(value.trim())
                            || "1".equals(value.trim());
                case "date":
                    // 日期保持字符串格�?由业务层处理
                    return value.trim();
                default:
                    return value.trim();
            }
        } catch (Exception e) {
            log.warn("值转换失�? {} -> {}, 错误: {}", value, fieldType, e.getMessage());
            return value;
        }
    }

    /**
     * 批处理回调接�?Map形式)
     */
    @FunctionalInterface
    public interface BatchCallback {
        void process(List<Map<String, Object>> data);
    }

    /**
     * 类型化批处理回调接口
     */
    @FunctionalInterface
    public interface TypedBatchCallback<T> {
        void process(List<T> data);
    }
}
