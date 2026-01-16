package com.gmrfid.excel.config;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel任务配置
 */
@Data
public class ExcelTaskConfig {

    /**
     * 任务类型(唯一标识)
     */
    private String type;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 上传是否使用文件缓冲
     */
    private boolean uploadUseFileBuffer = false;

    /**
     * 失败文件描述
     */
    private String failureFileDescription = "下载失败数据";

    /**
     * 是否验证重复任务
     */
    private boolean verifyDuplicateTask = false;

    /**
     * 最大并行运行数
     */
    private int maxParallelRunNum = 10;

    /**
     * 导出文件名称模板
     */
    private String exportFileName;

    /**
     * Sheet配置列表
     */
    private List<ExcelSheetConfig> sheets = new ArrayList<>();
}
