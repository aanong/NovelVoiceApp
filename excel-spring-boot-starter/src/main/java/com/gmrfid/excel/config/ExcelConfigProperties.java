package com.gmrfid.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel配置属性
 */
@Data
@ConfigurationProperties(prefix = "excel")
public class ExcelConfigProperties {

    /**
     * Excel任务列表
     */
    private List<ExcelTaskConfig> tasks = new ArrayList<>();

    /**
     * 任务配置缓存，提高查询效率
     */
    private Map<String, ExcelTaskConfig> taskMap = new ConcurrentHashMap<>();

    /**
     * 根据类型获取任务配置
     */
    public ExcelTaskConfig getTaskConfig(String taskType) {
        if (taskType == null) {
            return null;
        }

        // 先从缓存获取
        if (taskMap.containsKey(taskType)) {
            return taskMap.get(taskType);
        }

        // 缓存未命中，遍历查找并缓存
        if (tasks != null) {
            for (ExcelTaskConfig task : tasks) {
                if (taskType.equals(task.getType())) {
                    taskMap.put(taskType, task);
                    return task;
                }
            }
        }

        return null;
    }
}
