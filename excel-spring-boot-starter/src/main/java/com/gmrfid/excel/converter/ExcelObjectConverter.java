package com.gmrfid.excel.converter;

import com.gmrfid.excel.config.ExcelColumnConfig;
import com.gmrfid.excel.config.ExcelSheetConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel对象转换器
 * 处理Map与Java对象之间的相互转换
 */
@Slf4j
@Component
public class ExcelObjectConverter {

    /**
     * 将Map数据转换为指定类型的对象
     */
    public <T> T convertToObject(Map<String, Object> data, ExcelSheetConfig sheetConfig) {
        Class<?> clazz = sheetConfig.getOrmClassType();
        if (clazz == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            T bean = (T) clazz.getDeclaredConstructor().newInstance();
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
            wrapper.setAutoGrowNestedPaths(true);

            for (ExcelColumnConfig column : sheetConfig.getColumns()) {
                String field = column.getField();
                Object value = data.get(field);

                if (value != null && wrapper.isWritableProperty(field)) {
                    try {
                        wrapper.setPropertyValue(field, convertType(value, column));
                    } catch (Exception e) {
                        log.warn("属性设置失败: {} = {}, error: {}", field, value, e.getMessage());
                    }
                }
            }
            return bean;
        } catch (Exception e) {
            log.error("创建对象实例失败: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * 批量转换Map列表为对象列表
     */
    public <T> List<T> convertToObjectList(List<Map<String, Object>> dataList, ExcelSheetConfig sheetConfig) {
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
     * 将Java对象转换为Map (用于导出)
     */
    public Map<String, Object> convertToMap(Object bean, ExcelSheetConfig sheetConfig) {
        Map<String, Object> map = new LinkedHashMap<>();
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);

        for (ExcelColumnConfig column : sheetConfig.getColumns()) {
            String field = column.getField();
            if (wrapper.isReadableProperty(field)) {
                Object value = wrapper.getPropertyValue(field);
                map.put(field, value);
            }
        }
        return map;
    }

    /**
     * 批量转换对象列表为Map列表
     */
    public List<Map<String, Object>> convertToMapList(List<?> objList, ExcelSheetConfig sheetConfig) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object obj : objList) {
            result.add(convertToMap(obj, sheetConfig));
        }
        return result;
    }

    /**
     * 类型转换逻辑
     */
    private Object convertType(Object value, ExcelColumnConfig column) {
        if (value == null)
            return null;

        String type = column.getFieldType();
        String strVal = String.valueOf(value).trim();
        if (strVal.isEmpty())
            return null;

        try {
            switch (type.toLowerCase()) {
                case "int":
                case "integer":
                    return Integer.valueOf(strVal);
                case "long":
                    return Long.valueOf(strVal);
                case "double":
                    return Double.valueOf(strVal);
                case "boolean":
                    return "true".equalsIgnoreCase(strVal) || "1".equals(strVal) || "是".equals(strVal);
                case "date":
                    if (value instanceof Date)
                        return value;
                    return new SimpleDateFormat(column.getDateFormat()).parse(strVal);
                default:
                    return strVal;
            }
        } catch (Exception e) {
            log.warn("类型转换失败: {} -> {}, error: {}", strVal, type, e.getMessage());
            return value;
        }
    }
}
