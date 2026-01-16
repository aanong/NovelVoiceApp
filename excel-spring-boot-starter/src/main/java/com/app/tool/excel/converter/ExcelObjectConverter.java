package com.app.tool.excel.converter;

import com.app.tool.excel.config.ExcelColumnConfig;
import com.app.tool.excel.config.ExcelSheetConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel数据与对象之间的转换器
 * 支持根据YML配置中的ormClass将Map数据转换为Java对象
 */
@Slf4j
@Component
public class ExcelObjectConverter {

    /**
     * 将Map数据转换为指定类型的对象
     * 
     * @param data        Map形式的数据
     * @param sheetConfig Sheet配置（包含ormClass和列配置）
     * @param <T>         目标类型
     * @return 转换后的对象，如果转换失败返回null
     */
    @SuppressWarnings("unchecked")
    public <T> T convertToObject(Map<String, Object> data, ExcelSheetConfig sheetConfig) {
        Class<?> targetClass = sheetConfig.getOrmClassType();
        if (targetClass == null) {
            log.warn("Sheet配置中未指定有效的ormClass");
            return null;
        }

        try {
            Object instance = targetClass.getDeclaredConstructor().newInstance();
            BeanWrapper beanWrapper = new BeanWrapperImpl(instance);
            beanWrapper.setAutoGrowNestedPaths(true);

            // 创建字段名到列配置的映射
            Map<String, ExcelColumnConfig> fieldConfigMap = buildFieldConfigMap(sheetConfig);

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                // 跳过内部字段（如_errorMessage）
                if (fieldName.startsWith("_")) {
                    continue;
                }

                try {
                    if (beanWrapper.isWritableProperty(fieldName)) {
                        // 获取目标属性类型
                        Class<?> propertyType = beanWrapper.getPropertyType(fieldName);
                        // 获取列配置（如果有的话）
                        ExcelColumnConfig columnConfig = fieldConfigMap.get(fieldName);
                        // 转换值
                        Object convertedValue = convertValue(value, propertyType, columnConfig);
                        beanWrapper.setPropertyValue(fieldName, convertedValue);
                    }
                } catch (Exception e) {
                    log.debug("设置属性 {} 失败: {}", fieldName, e.getMessage());
                }
            }

            return (T) instance;
        } catch (Exception e) {
            log.error("转换对象失败, 目标类: {}", targetClass.getName(), e);
            return null;
        }
    }

    /**
     * 批量转换Map数据为对象列表
     * 
     * @param dataList    Map数据列表
     * @param sheetConfig Sheet配置
     * @param <T>         目标类型
     * @return 转换后的对象列表
     */
    public <T> List<T> convertToObjectList(List<Map<String, Object>> dataList, ExcelSheetConfig sheetConfig) {
        if (dataList == null || dataList.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            T obj = convertToObject(data, sheetConfig);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * 将对象转换为Map
     * 
     * @param obj         要转换的对象
     * @param sheetConfig Sheet配置（可选，用于确定导出字段）
     * @return Map形式的数据
     */
    public Map<String, Object> convertToMap(Object obj, ExcelSheetConfig sheetConfig) {
        if (obj == null) {
            return new LinkedHashMap<>();
        }

        Map<String, Object> result = new LinkedHashMap<>();

        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(obj);
            PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

            // 如果有列配置，只导出配置的字段
            Set<String> allowedFields = getAllowedFields(sheetConfig);

            for (PropertyDescriptor pd : propertyDescriptors) {
                String propertyName = pd.getName();
                if ("class".equals(propertyName)) {
                    continue;
                }

                // 检查是否在允许的字段列表中
                if (!allowedFields.isEmpty() && !allowedFields.contains(propertyName)) {
                    continue;
                }

                try {
                    Object value = beanWrapper.getPropertyValue(propertyName);
                    // 获取列配置用于格式化
                    ExcelColumnConfig columnConfig = getColumnConfig(sheetConfig, propertyName);
                    result.put(propertyName, formatValue(value, columnConfig));
                } catch (Exception e) {
                    log.debug("读取属性 {} 失败: {}", propertyName, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("转换对象为Map失败: {}", e.getMessage());
        }

        return result;
    }

    /**
     * 批量转换对象为Map列表
     * 
     * @param objList     对象列表
     * @param sheetConfig Sheet配置
     * @return Map列表
     */
    public List<Map<String, Object>> convertToMapList(List<?> objList, ExcelSheetConfig sheetConfig) {
        if (objList == null || objList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object obj : objList) {
            result.add(convertToMap(obj, sheetConfig));
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 构建字段名到列配置的映射
     */
    private Map<String, ExcelColumnConfig> buildFieldConfigMap(ExcelSheetConfig sheetConfig) {
        Map<String, ExcelColumnConfig> map = new HashMap<>();
        if (sheetConfig != null && sheetConfig.getColumns() != null) {
            for (ExcelColumnConfig column : sheetConfig.getColumns()) {
                if (column.getField() != null) {
                    map.put(column.getField(), column);
                }
            }
        }
        return map;
    }

    /**
     * 获取允许导出的字段集合
     */
    private Set<String> getAllowedFields(ExcelSheetConfig sheetConfig) {
        Set<String> fields = new HashSet<>();
        if (sheetConfig != null && sheetConfig.getColumns() != null) {
            for (ExcelColumnConfig column : sheetConfig.getColumns()) {
                if (column.isExportable() && column.getField() != null) {
                    fields.add(column.getField());
                }
            }
        }
        return fields;
    }

    /**
     * 根据字段名获取列配置
     */
    private ExcelColumnConfig getColumnConfig(ExcelSheetConfig sheetConfig, String fieldName) {
        if (sheetConfig == null || sheetConfig.getColumns() == null) {
            return null;
        }
        for (ExcelColumnConfig column : sheetConfig.getColumns()) {
            if (fieldName.equals(column.getField())) {
                return column;
            }
        }
        return null;
    }

    /**
     * 将值转换为目标类型
     */
    private Object convertValue(Object value, Class<?> targetType, ExcelColumnConfig columnConfig) {
        if (value == null) {
            return null;
        }

        String strValue = String.valueOf(value).trim();
        if (strValue.isEmpty()) {
            return null;
        }

        try {
            // 字符串类型
            if (targetType == String.class) {
                return strValue;
            }

            // 整数类型
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(strValue);
            }

            // Long类型
            if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(strValue);
            }

            // Double类型
            if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(strValue);
            }

            // Float类型
            if (targetType == Float.class || targetType == float.class) {
                return Float.parseFloat(strValue);
            }

            // Boolean类型
            if (targetType == Boolean.class || targetType == boolean.class) {
                return "是".equals(strValue) || "true".equalsIgnoreCase(strValue) || "1".equals(strValue);
            }

            // BigDecimal类型
            if (targetType == java.math.BigDecimal.class) {
                return new java.math.BigDecimal(strValue);
            }

            // Date类型
            if (targetType == Date.class) {
                String dateFormat = (columnConfig != null && StringUtils.hasText(columnConfig.getDateFormat()))
                        ? columnConfig.getDateFormat()
                        : "yyyy/MM/dd";
                return parseDate(strValue, dateFormat);
            }

            // LocalDate类型
            if (targetType == java.time.LocalDate.class) {
                String dateFormat = (columnConfig != null && StringUtils.hasText(columnConfig.getDateFormat()))
                        ? columnConfig.getDateFormat()
                        : "yyyy/MM/dd";
                return java.time.LocalDate.parse(strValue,
                        java.time.format.DateTimeFormatter
                                .ofPattern(dateFormat.replace("yyyy/MM/dd", "yyyy'/'MM'/'dd")));
            }

            // LocalDateTime类型
            if (targetType == java.time.LocalDateTime.class) {
                String dateFormat = (columnConfig != null && StringUtils.hasText(columnConfig.getDateFormat()))
                        ? columnConfig.getDateFormat()
                        : "yyyy/MM/dd HH:mm:ss";
                return java.time.LocalDateTime.parse(strValue,
                        java.time.format.DateTimeFormatter.ofPattern(dateFormat));
            }

            // 其他类型，直接返回字符串
            return strValue;

        } catch (Exception e) {
            log.debug("类型转换失败: {} -> {}, 值: {}", value.getClass().getSimpleName(),
                    targetType.getSimpleName(), strValue);
            return strValue;
        }
    }

    /**
     * 解析日期字符串
     */
    private Date parseDate(String dateStr, String format) throws ParseException {
        // 尝试多种常见格式
        String[] formats = { format, "yyyy/MM/dd", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss" };
        for (String fmt : formats) {
            try {
                return new SimpleDateFormat(fmt).parse(dateStr);
            } catch (ParseException ignored) {
            }
        }
        throw new ParseException("无法解析日期: " + dateStr, 0);
    }

    /**
     * 格式化值用于导出
     */
    private Object formatValue(Object value, ExcelColumnConfig columnConfig) {
        if (value == null) {
            return "";
        }

        // 日期格式化
        if (value instanceof Date && columnConfig != null && StringUtils.hasText(columnConfig.getDateFormat())) {
            return new SimpleDateFormat(columnConfig.getDateFormat()).format((Date) value);
        }

        // LocalDate格式化
        if (value instanceof java.time.LocalDate && columnConfig != null
                && StringUtils.hasText(columnConfig.getDateFormat())) {
            return ((java.time.LocalDate) value).format(
                    java.time.format.DateTimeFormatter.ofPattern(columnConfig.getDateFormat()));
        }

        // LocalDateTime格式化
        if (value instanceof java.time.LocalDateTime && columnConfig != null
                && StringUtils.hasText(columnConfig.getDateFormat())) {
            return ((java.time.LocalDateTime) value).format(
                    java.time.format.DateTimeFormatter.ofPattern(columnConfig.getDateFormat()));
        }

        // 布尔值转换
        if (value instanceof Boolean) {
            return (Boolean) value ? "是" : "否";
        }

        return value;
    }
}
