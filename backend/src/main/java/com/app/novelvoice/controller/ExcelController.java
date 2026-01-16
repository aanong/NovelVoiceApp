package com.app.novelvoice.controller;

import com.app.novelvoice.common.Result;
import com.gmrfid.excel.dto.ExcelExportRequest;
import com.gmrfid.excel.dto.ExcelImportResult;
import com.gmrfid.excel.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出统一控制器
 * 提供通用的Excel文件导入、导出、模板下载功能
 */
@Slf4j
@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    /**
     * 导入Excel文件
     * 
     * @param file       上传的Excel文件
     * @param taskType   任务类型(对应yml配置中的type)
     * @param sheetIndex Sheet索引(默认0)
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<ExcelImportResult<Map<String, Object>>> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskType") String taskType,
            @RequestParam(value = "sheetIndex", required = false, defaultValue = "0") Integer sheetIndex) {

        log.info("开始导入Excel, 任务类型: {}, 文件名: {}", taskType, file.getOriginalFilename());

        // 验证文件
        if (file.isEmpty()) {
            return Result.error(400, "请上传文件");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error(400, "只支持Excel文件(.xlsx或.xls)");
        }

        // 执行导入
        ExcelImportResult<Map<String, Object>> result = excelService.importExcel(file, taskType, sheetIndex, null);

        if (result.isSuccess()) {
            log.info("Excel导入成功, 总行数: {}, 成功: {}", result.getTotalRows(), result.getSuccessRows());
            return Result.success(result);
        } else {
            log.warn("Excel导入存在失败数据, 总行数: {}, 成功: {}, 失败: {}",
                    result.getTotalRows(), result.getSuccessRows(), result.getFailedRows());
            return Result.success(result);
        }
    }

    /**
     * 导出Excel文件
     * 
     * @param response HTTP响应
     * @param request  导出请求
     */
    @PostMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestBody ExcelExportRequest request) {
        log.info("开始导出Excel, 任务类型: {}", request.getTaskType());
        excelService.exportExcel(response, request);
    }

    /**
     * 下载导入模板
     * 
     * @param response HTTP响应
     * @param taskType 任务类型
     */
    @GetMapping("/template/{taskType}")
    public void downloadTemplate(HttpServletResponse response,
            @PathVariable("taskType") String taskType) {
        log.info("下载导入模板, 任务类型: {}", taskType);
        excelService.downloadTemplate(response, taskType);
    }

    /**
     * 获取所有任务类型
     * 
     * @return 任务类型列表
     */
    @GetMapping("/task-types")
    public Result<List<Map<String, String>>> getTaskTypes() {
        return Result.success(excelService.getTaskTypes());
    }

    /**
     * 导入并处理数据(带业务回调)
     * 用于需要在导入过程中实时处理数据的场景
     * 
     * @param file       上传的Excel文件
     * @param taskType   任务类型
     * @param sheetIndex Sheet索引
     * @return 导入结果
     */
    @PostMapping("/import-with-callback")
    public Result<ExcelImportResult<Map<String, Object>>> importWithCallback(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskType") String taskType,
            @RequestParam(value = "sheetIndex", required = false, defaultValue = "0") Integer sheetIndex) {

        log.info("开始导入Excel(带回调), 任务类型: {}", taskType);

        // 验证文件
        if (file.isEmpty()) {
            return Result.error(400, "请上传文件");
        }

        // 批处理回调示例:每批次处理1000条数据
        ExcelImportResult<Map<String, Object>> result = excelService.importExcel(
                file, taskType, sheetIndex,
                batchData -> {
                    // 这里可以调用具体的业务处理逻辑
                    log.info("批处理回调, 本批次数据量: {}", batchData.size());
                    // 例如: businessService.batchSave(batchData);
                });

        return Result.success(result);
    }
}
