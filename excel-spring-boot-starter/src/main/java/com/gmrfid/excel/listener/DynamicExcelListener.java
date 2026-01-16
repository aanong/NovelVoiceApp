package com.gmrfid.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.gmrfid.excel.config.ExcelColumnConfig;
import com.gmrfid.excel.config.ExcelSheetConfig;
import com.gmrfid.excel.converter.ExcelObjectConverter;
import com.gmrfid.excel.dto.ExcelImportResult;
import com.gmrfid.excel.validator.ExcelExpressionValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态Excel监听器
 * 支持通用的Map转换和基于配置的验证
 */
@Slf4j
public class DynamicExcelListener extends AnalysisEventListener<Map<Integer, Object>> {

    private final ExcelSheetConfig sheetConfig;
    private final ExcelExpressionValidator validator;
    private final ExcelObjectConverter converter;

    private final ExcelImportResult<Map<String, Object>> result = new ExcelImportResult<>();
    private final ExcelImportResult<Object> typedResult = new ExcelImportResult<>();

    // 批处理相关
    private final List<Map<String, Object>> cachedDataList = new ArrayList<>();
    private final List<Object> cachedTypedList = new ArrayList<>();
    private final BatchCallback batchCallback;
    private final TypedBatchCallback<Object> typedBatchCallback;
    private final int batchSize = 1000;

    /**
     * Map形式构造
     */
    public DynamicExcelListener(ExcelSheetConfig sheetConfig, ExcelExpressionValidator validator,
            BatchCallback batchCallback) {
        this.sheetConfig = sheetConfig;
        this.validator = validator;
        this.converter = null;
        this.batchCallback = batchCallback;
        this.typedBatchCallback = null;
    }

    /**
     * 类型化形式构?
     */
    @SuppressWarnings("unchecked")
    public DynamicExcelListener(ExcelSheetConfig sheetConfig, ExcelExpressionValidator validator,
            ExcelObjectConverter converter, TypedBatchCallback<?> typedBatchCallback) {
        this.sheetConfig = sheetConfig;
        this.validator = validator;
        this.converter = converter;
        this.batchCallback = null;
        this.typedBatchCallback = (TypedBatchCallback<Object>) typedBatchCallback;
    }

    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        result.incrementTotal();
        typedResult.incrementTotal();

        Map<String, Object> rowData = mapToNamedFields(data);

        // 1. 验证数据
        String error = validateRow(rowData);
        if (error != null) {
            result.addFailed(rowData, error);
            typedResult.addFailed(rowData, error);
            return;
        }

        // 2. 处理数据
        result.addSuccess(rowData);

        // 如果是类型化导入
        if (converter != null && sheetConfig.hasOrmClass()) {
            Object obj = converter.convertToObject(rowData, sheetConfig);
            if (obj != null) {
                typedResult.addSuccess(obj);
                if (typedBatchCallback != null) {
                    cachedTypedList.add(obj);
                    if (cachedTypedList.size() >= batchSize) {
                        typedBatchCallback.execute(new ArrayList<>(cachedTypedList));
                        cachedTypedList.clear();
                    }
                }
            }
        }

        // 3. 批处理回?(Map形式)
        if (batchCallback != null) {
            cachedDataList.add(rowData);
            if (cachedDataList.size() >= batchSize) {
                batchCallback.execute(new ArrayList<>(cachedDataList));
                cachedDataList.clear();
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 处理剩余数据
        if (batchCallback != null && !cachedDataList.isEmpty()) {
            batchCallback.execute(cachedDataList);
        }
        if (typedBatchCallback != null && !cachedTypedList.isEmpty()) {
            typedBatchCallback.execute(cachedTypedList);
        }
        log.info("Excel解析完成, 共处理{}行", result.getTotalRows());
    }

    /**
     * 将索引Map转换为字段名Map
     */
    private Map<String, Object> mapToNamedFields(Map<Integer, Object> data) {
        Map<String, Object> rowData = new LinkedHashMap<>();
        List<ExcelColumnConfig> columns = sheetConfig.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            ExcelColumnConfig col = columns.get(i);
            Object value = data.get(i);
            rowData.put(col.getField(), value);
        }
        return rowData;
    }

    /**
     * 校验整行数据
     */
    private String validateRow(Map<String, Object> rowData) {
        for (ExcelColumnConfig col : sheetConfig.getColumns()) {
            if (!StringUtils.hasText(col.getVerifyExpression())) {
                continue;
            }

            Object value = rowData.get(col.getField());
            boolean valid = validator.validate(col.getVerifyExpression(), value, rowData);

            if (!valid) {
                return col.getErrorMessage() != null ? col.getErrorMessage()
                        : "字段[" + col.getTitle() + "]验证失败: " + value;
            }
        }
        return null;
    }

    public ExcelImportResult<Map<String, Object>> getResult() {
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> ExcelImportResult<T> getTypedResult() {
        return (ExcelImportResult<T>) typedResult;
    }

    /**
     * 批处理回调接?
     */
    @FunctionalInterface
    public interface BatchCallback {
        void execute(List<Map<String, Object>> dataList);
    }

    /**
     * 类型化批处理回调接?
     */
    @FunctionalInterface
    public interface TypedBatchCallback<T> {
        void execute(List<T> dataList);
    }
}
