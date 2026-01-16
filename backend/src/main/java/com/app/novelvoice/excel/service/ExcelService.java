package com.app.novelvoice.excel.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.excel.config.ExcelColumnConfig;
import com.app.novelvoice.excel.config.ExcelConfigProperties;
import com.app.novelvoice.excel.config.ExcelSheetConfig;
import com.app.novelvoice.excel.config.ExcelTaskConfig;
import com.app.novelvoice.excel.converter.ExcelObjectConverter;
import com.app.novelvoice.excel.dto.ExcelExportRequest;
import com.app.novelvoice.excel.dto.ExcelImportResult;
import com.app.novelvoice.excel.listener.DynamicExcelListener;
import com.app.novelvoice.excel.validator.ExcelExpressionValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel导入导出统一服务
 * 基于YML配置实现动态的Excel导入导出功能
 * 支持将导入数据转换为ormClass指定的Java对象
 */
@Slf4j
@Service
public class ExcelService {

    @Autowired
    private ExcelConfigProperties excelConfig;

    @Autowired
    private ExcelExpressionValidator expressionValidator;

    @Autowired
    private ExcelObjectConverter objectConverter;

    /**
     * 导入Excel文件(返回Map形式)
     * 
     * @param file          上传的文件
     * @param taskType      任务类型
     * @param sheetIndex    Sheet索引(默认0)
     * @param batchCallback 批处理回调(可选)
     * @return 导入结果
     */
    public ExcelImportResult<Map<String, Object>> importExcel(
            MultipartFile file,
            String taskType,
            Integer sheetIndex,
            DynamicExcelListener.BatchCallback batchCallback) {

        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(taskType);
        if (taskConfig == null) {
            return ExcelImportResult.fail("未找到任务配置: " + taskType);
        }

        // 获取Sheet配置
        int sheetIdx = sheetIndex != null ? sheetIndex : 0;
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, sheetIdx);
        if (sheetConfig == null) {
            return ExcelImportResult.fail("未找到Sheet配置: " + sheetIdx);
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 创建监听器
            DynamicExcelListener listener = new DynamicExcelListener(
                    sheetConfig, expressionValidator, batchCallback);

            // 读取Excel
            EasyExcel.read(inputStream, listener)
                    .sheet(sheetIdx)
                    .headRowNumber(sheetConfig.getHeadRowNumber())
                    .doRead();

            return listener.getResult();

        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            return ExcelImportResult.fail("读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 导入Excel文件(简化版,返回Map形式)
     */
    public ExcelImportResult<Map<String, Object>> importExcel(MultipartFile file, String taskType) {
        return importExcel(file, taskType, 0, null);
    }

    /**
     * 导入Excel文件并转换为指定类型的对象列表
     * 根据YML配置中的ormClass自动进行类型转换
     * 
     * @param file               上传的文件
     * @param taskType           任务类型
     * @param sheetIndex         Sheet索引(默认0)
     * @param typedBatchCallback 类型化批处理回调(可选)
     * @param <T>                目标类型(由ormClass指定)
     * @return 类型化的导入结果
     */
    public <T> ExcelImportResult<T> importExcelAsObject(
            MultipartFile file,
            String taskType,
            Integer sheetIndex,
            DynamicExcelListener.TypedBatchCallback<T> typedBatchCallback) {

        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(taskType);
        if (taskConfig == null) {
            return ExcelImportResult.fail("未找到任务配置: " + taskType);
        }

        // 获取Sheet配置
        int sheetIdx = sheetIndex != null ? sheetIndex : 0;
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, sheetIdx);
        if (sheetConfig == null) {
            return ExcelImportResult.fail("未找到Sheet配置: " + sheetIdx);
        }

        // 检查是否配置了ormClass
        if (!sheetConfig.hasOrmClass()) {
            return ExcelImportResult.fail("未配置有效的ormClass: " + sheetConfig.getOrmClass());
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 创建支持类型化转换的监听器
            DynamicExcelListener listener = new DynamicExcelListener(
                    sheetConfig, expressionValidator, objectConverter, typedBatchCallback);

            // 读取Excel
            EasyExcel.read(inputStream, listener)
                    .sheet(sheetIdx)
                    .headRowNumber(sheetConfig.getHeadRowNumber())
                    .doRead();

            return listener.getTypedResult();

        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            return ExcelImportResult.fail("读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 导入Excel文件并转换为指定类型的对象列表(简化版)
     * 
     * @param file     上传的文件
     * @param taskType 任务类型
     * @param <T>      目标类型(由ormClass指定)
     * @return 类型化的导入结果
     */
    public <T> ExcelImportResult<T> importExcelAsObject(MultipartFile file, String taskType) {
        return importExcelAsObject(file, taskType, 0, null);
    }

    /**
     * 导入Excel文件并直接返回对象列表
     * 
     * @param file     上传的文件
     * @param taskType 任务类型
     * @param <T>      目标类型(由ormClass指定)
     * @return 转换后的对象列表，如果导入失败则返回空列表
     */
    public <T> List<T> importExcelAsObjectList(MultipartFile file, String taskType) {
        ExcelImportResult<T> result = importExcelAsObject(file, taskType);
        if (result != null && result.isSuccess()) {
            return result.getSuccessData();
        }
        return new ArrayList<>();
    }

    /**
     * 将Map数据列表转换为指定类型的对象列表
     * 
     * @param dataList   Map数据列表
     * @param taskType   任务类型
     * @param sheetIndex Sheet索引
     * @param <T>        目标类型
     * @return 转换后的对象列表
     */
    public <T> List<T> convertToObjectList(List<Map<String, Object>> dataList,
            String taskType,
            Integer sheetIndex) {
        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(taskType);
        if (taskConfig == null) {
            log.warn("未找到任务配置: {}", taskType);
            return new ArrayList<>();
        }

        // 获取Sheet配置
        int sheetIdx = sheetIndex != null ? sheetIndex : 0;
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, sheetIdx);
        if (sheetConfig == null || !sheetConfig.hasOrmClass()) {
            log.warn("未找到有效的Sheet配置或ormClass");
            return new ArrayList<>();
        }

        return objectConverter.convertToObjectList(dataList, sheetConfig);
    }

    /**
     * 将对象列表转换为Map数据列表(用于导出)
     * 
     * @param objList    对象列表
     * @param taskType   任务类型
     * @param sheetIndex Sheet索引
     * @return Map数据列表
     */
    public List<Map<String, Object>> convertToMapList(List<?> objList,
            String taskType,
            Integer sheetIndex) {
        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(taskType);
        if (taskConfig == null) {
            log.warn("未找到任务配置: {}", taskType);
            return new ArrayList<>();
        }

        // 获取Sheet配置
        int sheetIdx = sheetIndex != null ? sheetIndex : 0;
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, sheetIdx);
        if (sheetConfig == null) {
            log.warn("未找到有效的Sheet配置");
            return new ArrayList<>();
        }

        return objectConverter.convertToMapList(objList, sheetConfig);
    }

    /**
     * 导出Excel到响应流
     * 
     * @param response HTTP响应
     * @param request  导出请求
     */
    public void exportExcel(HttpServletResponse response, ExcelExportRequest request) {
        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(request.getTaskType());
        if (taskConfig == null) {
            throw new BusinessException("未找到任务配置: " + request.getTaskType());
        }

        // 获取Sheet配置
        int sheetIdx = request.getSheetIndex() != null ? request.getSheetIndex() : 0;
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, sheetIdx);
        if (sheetConfig == null) {
            throw new BusinessException("未找到Sheet配置: " + sheetIdx);
        }

        // 生成文件名
        String fileName = generateFileName(request.getFileName(), taskConfig);

        try {
            // 设置响应头
            setExportResponse(response, fileName);

            // 获取导出数据
            List<List<Object>> exportData = prepareExportData(request, sheetConfig);

            // 获取表头
            List<List<String>> headers = prepareHeaders(sheetConfig, request);

            // 导出
            EasyExcel.write(response.getOutputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(sheetConfig.getName())
                    .head(headers)
                    .registerWriteHandler(createCellStyleStrategy())
                    .doWrite(exportData);

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出Excel到输出流
     * 
     * @param outputStream 输出流
     * @param request      导出请求
     */
    public void exportExcel(OutputStream outputStream, ExcelExportRequest request) {
        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(request.getTaskType());
        if (taskConfig == null) {
            throw new BusinessException("未找到任务配置: " + request.getTaskType());
        }

        // 获取Sheet配置
        int sheetIdx = request.getSheetIndex() != null ? request.getSheetIndex() : 0;
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, sheetIdx);
        if (sheetConfig == null) {
            throw new BusinessException("未找到Sheet配置: " + sheetIdx);
        }

        // 获取导出数据
        List<List<Object>> exportData = prepareExportData(request, sheetConfig);

        // 获取表头
        List<List<String>> headers = prepareHeaders(sheetConfig, request);

        // 导出
        EasyExcel.write(outputStream)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet(sheetConfig.getName())
                .head(headers)
                .registerWriteHandler(createCellStyleStrategy())
                .doWrite(exportData);
    }

    /**
     * 导出失败数据
     * 
     * @param response     HTTP响应
     * @param importResult 导入结果
     * @param taskType     任务类型
     */
    public void exportFailedData(HttpServletResponse response,
            ExcelImportResult<?> importResult,
            String taskType) {
        if (importResult.getFailedRawData().isEmpty()) {
            throw new BusinessException("没有失败数据需要导出");
        }

        ExcelExportRequest request = new ExcelExportRequest();
        request.setTaskType(taskType);
        request.setMapData(importResult.getFailedRawData());
        request.setFileName("失败数据");

        exportExcel(response, request);
    }

    /**
     * 下载导入模板
     * 
     * @param response HTTP响应
     * @param taskType 任务类型
     */
    public void downloadTemplate(HttpServletResponse response, String taskType) {
        // 获取任务配置
        ExcelTaskConfig taskConfig = excelConfig.getTaskConfig(taskType);
        if (taskConfig == null) {
            throw new BusinessException("未找到任务配置: " + taskType);
        }

        // 获取第一个Sheet配置
        ExcelSheetConfig sheetConfig = getSheetConfig(taskConfig, 0);
        if (sheetConfig == null) {
            throw new BusinessException("未找到Sheet配置");
        }

        String fileName = (taskConfig.getName() != null ? taskConfig.getName() : "导入模板") + "_模板";

        try {
            setExportResponse(response, fileName);

            // 获取表头
            List<List<String>> headers = new ArrayList<>();
            if (sheetConfig.getColumns() != null) {
                for (ExcelColumnConfig column : sheetConfig.getColumns()) {
                    if (column.isImportable()) {
                        headers.add(Collections.singletonList(column.getTitle()));
                    }
                }
            }

            // 导出空模板
            EasyExcel.write(response.getOutputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(sheetConfig.getName())
                    .head(headers)
                    .registerWriteHandler(createCellStyleStrategy())
                    .doWrite(new ArrayList<>());

        } catch (IOException e) {
            log.error("下载模板失败", e);
            throw new BusinessException("下载模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有任务类型
     */
    public List<Map<String, String>> getTaskTypes() {
        List<Map<String, String>> result = new ArrayList<>();
        if (excelConfig.getTasks() != null) {
            for (ExcelTaskConfig task : excelConfig.getTasks()) {
                Map<String, String> item = new HashMap<>();
                item.put("type", task.getType());
                item.put("name", task.getName());
                result.add(item);
            }
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 获取Sheet配置
     */
    private ExcelSheetConfig getSheetConfig(ExcelTaskConfig taskConfig, int sheetIndex) {
        if (taskConfig.getSheets() == null || taskConfig.getSheets().isEmpty()) {
            return null;
        }
        for (ExcelSheetConfig sheet : taskConfig.getSheets()) {
            if (sheet.getSheetIndex() != null && sheet.getSheetIndex() == sheetIndex) {
                return sheet;
            }
        }
        // 如果没找到匹配的,返回第一个
        if (sheetIndex == 0) {
            return taskConfig.getSheets().get(0);
        }
        return null;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String customFileName, ExcelTaskConfig taskConfig) {
        String baseName;
        if (StringUtils.hasText(customFileName)) {
            baseName = customFileName;
        } else if (StringUtils.hasText(taskConfig.getExportFileName())) {
            baseName = taskConfig.getExportFileName();
        } else if (StringUtils.hasText(taskConfig.getName())) {
            baseName = taskConfig.getName();
        } else {
            baseName = "导出数据";
        }

        // 添加时间戳
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return baseName + "_" + timestamp;
    }

    /**
     * 设置导出响应头
     */
    private void setExportResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                .replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition",
                "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
    }

    /**
     * 准备导出数据
     */
    private List<List<Object>> prepareExportData(ExcelExportRequest request,
            ExcelSheetConfig sheetConfig) {
        List<Map<String, Object>> dataList;

        if (request.getMapData() != null && !request.getMapData().isEmpty()) {
            dataList = request.getMapData();
        } else if (request.getData() != null && !request.getData().isEmpty()) {
            // 将实体转换为Map
            dataList = convertToMapList(request.getData());
        } else {
            return new ArrayList<>();
        }

        // 获取要导出的列配置
        List<ExcelColumnConfig> exportColumns = getExportColumns(sheetConfig, request);

        // 转换为EasyExcel需要的格式
        List<List<Object>> result = new ArrayList<>();
        for (Map<String, Object> row : dataList) {
            List<Object> rowData = new ArrayList<>();
            for (ExcelColumnConfig column : exportColumns) {
                Object value = row.get(column.getField());
                rowData.add(formatValue(value, column));
            }
            result.add(rowData);
        }

        return result;
    }

    /**
     * 准备表头
     */
    private List<List<String>> prepareHeaders(ExcelSheetConfig sheetConfig,
            ExcelExportRequest request) {
        List<ExcelColumnConfig> exportColumns = getExportColumns(sheetConfig, request);
        List<List<String>> headers = new ArrayList<>();
        for (ExcelColumnConfig column : exportColumns) {
            headers.add(Collections.singletonList(column.getTitle()));
        }
        return headers;
    }

    /**
     * 获取要导出的列
     */
    private List<ExcelColumnConfig> getExportColumns(ExcelSheetConfig sheetConfig,
            ExcelExportRequest request) {
        if (sheetConfig.getColumns() == null) {
            return new ArrayList<>();
        }

        return sheetConfig.getColumns().stream()
                .filter(ExcelColumnConfig::isExportable)
                .filter(col -> {
                    // 检查包含字段
                    if (request.getIncludeFields() != null && !request.getIncludeFields().isEmpty()) {
                        return request.getIncludeFields().contains(col.getField());
                    }
                    return true;
                })
                .filter(col -> {
                    // 检查排除字段
                    if (request.getExcludeFields() != null && !request.getExcludeFields().isEmpty()) {
                        return !request.getExcludeFields().contains(col.getField());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将实体列表转换为Map列表
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> convertToMapList(List<?> dataList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : dataList) {
            if (item instanceof Map) {
                result.add((Map<String, Object>) item);
            } else {
                // 使用反射转换实体为Map
                result.add(beanToMap(item));
            }
        }
        return result;
    }

    /**
     * Bean转Map
     */
    private Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            java.beans.BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(bean.getClass());
            java.beans.PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (java.beans.PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!"class".equals(key)) {
                    java.lang.reflect.Method getter = property.getReadMethod();
                    if (getter != null) {
                        Object value = getter.invoke(bean);
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Bean转Map失败: {}", e.getMessage());
        }
        return map;
    }

    /**
     * 格式化值用于导出
     */
    private Object formatValue(Object value, ExcelColumnConfig column) {
        if (value == null) {
            return "";
        }

        // 日期格式化
        if (value instanceof Date && StringUtils.hasText(column.getDateFormat())) {
            return new SimpleDateFormat(column.getDateFormat()).format((Date) value);
        }

        // 布尔值转换
        if (value instanceof Boolean) {
            return (Boolean) value ? "是" : "否";
        }

        return value;
    }

    /**
     * 创建单元格样式策略
     */
    private HorizontalCellStyleStrategy createCellStyleStrategy() {
        // 表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        WriteFont headFont = new WriteFont();
        headFont.setFontHeightInPoints((short) 12);
        headFont.setBold(true);
        headWriteCellStyle.setWriteFont(headFont);

        // 内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        WriteFont contentFont = new WriteFont();
        contentFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(contentFont);

        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
}
