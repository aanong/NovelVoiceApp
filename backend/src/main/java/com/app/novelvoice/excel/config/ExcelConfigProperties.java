package com.app.novelvoice.excel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Excel配置属性类
 * 从application.yml或excel-config.yml中读取配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "excel")
public class ExcelConfigProperties {
    
    /**
     * 任务配置列表
     */
    private List<ExcelTaskConfig> tasks;
    
    /**
     * 任务配置缓存(按type索引)
     */
    private final Map<String, ExcelTaskConfig> taskConfigCache = new ConcurrentHashMap<>();
    
    /**
     * 根据任务类型获取配置
     * @param type 任务类型
     * @return 任务配置
     */
    public ExcelTaskConfig getTaskConfig(String type) {
        // 先从缓存获取
        ExcelTaskConfig cached = taskConfigCache.get(type);
        if (cached != null) {
            return cached;
        }
        
        // 遍历查找并缓存
        if (tasks != null) {
            for (ExcelTaskConfig task : tasks) {
                if (type.equals(task.getType())) {
                    taskConfigCache.put(type, task);
                    return task;
                }
            }
        }
        return null;
    }
    
    /**
     * 清除配置缓存
     */
    public void clearCache() {
        taskConfigCache.clear();
    }
}
