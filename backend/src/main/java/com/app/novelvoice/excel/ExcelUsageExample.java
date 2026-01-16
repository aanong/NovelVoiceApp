package com.app.novelvoice.excel;

import com.gmrfid.excel.dto.ExcelExportRequest;
import com.gmrfid.excel.dto.ExcelImportResult;
import com.gmrfid.excel.listener.DynamicExcelListener;
import com.gmrfid.excel.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出使用示例
 * 使用 excel-spring-boot-starter 实现
 * 
 * 支持两种导入方式:
 * 1. Map形式 - 返回 Map<String, Object> 列表, 适合动态字段场景
 * 2. 类型化形式 - 根据配置的ormClass返回Java对象列表, 适合固定结构场景
 */
@Slf4j
@Service
public class ExcelUsageExample {

    @Autowired
    private ExcelService excelService;

    // ==================== Map形式导入 ====================

    /**
     * 示例1: 基础导入(返回Map)
     * 导入用户数据,返回解析后的Map数据
     */
    public ExcelImportResult<Map<String, Object>> importUsers(MultipartFile file) {
        return excelService.importExcel(file, "user_import");
    }

    /**
     * 示例2: 带回调的导入(批量入库)
     */
    public ExcelImportResult<Map<String, Object>> importUsersWithSave(MultipartFile file) {
        return excelService.importExcel(file, "user_import", 0, batchData -> {
            log.info("批量保存用户数据, 本批次数量: {}", batchData.size());
            for (Map<String, Object> row : batchData) {
                String username = (String) row.get("username");
                log.debug("处理用户: {}", username);
            }
        });
    }

    // ==================== 类型化导入 ====================

    /**
     * 示例3: 类型化导入 - 根据ormClass配置自动转换
     * 配置文件: task-excel-async-config.yml
     */
    public <T> ExcelImportResult<T> importAsObject(MultipartFile file, String taskType) {
        return excelService.importExcelAsObject(file, taskType);
    }

    /**
     * 示例4: 直接获取对象列表
     */
    public <T> List<T> importAsObjectList(MultipartFile file, String taskType) {
        return excelService.importExcelAsObjectList(file, taskType);
    }

    /**
     * 示例5: 类型化导入带批处理回调
     */
    public <T> ExcelImportResult<T> importWithTypedBatch(
            MultipartFile file,
            String taskType,
            DynamicExcelListener.TypedBatchCallback<T> callback) {
        return excelService.importExcelAsObject(file, taskType, 0, callback);
    }

    // ==================== 导出功能 ====================

    /**
     * 示例6: 导出数据
     */
    public void exportData(HttpServletResponse response, String taskType, List<Map<String, Object>> data) {
        ExcelExportRequest request = new ExcelExportRequest();
        request.setTaskType(taskType);
        request.setMapData(data);
        excelService.exportExcel(response, request);
    }

    /**
     * 示例7: 导出失败数据
     */
    public void exportFailedData(HttpServletResponse response,
            ExcelImportResult<?> importResult,
            String taskType) {
        if (importResult.getFailedRows() > 0) {
            excelService.exportFailedData(response, importResult, taskType);
        }
    }

    /**
     * 示例8: 下载导入模板
     */
    public void downloadTemplate(HttpServletResponse response, String taskType) {
        excelService.downloadTemplate(response, taskType);
    }
}
