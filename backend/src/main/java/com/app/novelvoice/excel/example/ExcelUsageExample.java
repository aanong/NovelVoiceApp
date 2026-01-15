package com.app.novelvoice.excel.example;

import com.app.novelvoice.excel.dto.ExcelExportRequest;
import com.app.novelvoice.excel.dto.ExcelImportResult;
import com.app.novelvoice.excel.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出使用示例
 * 演示如何在业务代码中使用ExcelService
 */
@Slf4j
@Service
public class ExcelUsageExample {
    
    @Autowired
    private ExcelService excelService;
    
    /**
     * 示例1: 基础导入
     * 导入用户数据,返回解析后的数据
     */
    public ExcelImportResult<Map<String, Object>> importUsers(MultipartFile file) {
        // 直接调用导入服务,使用yml中配置的user_import任务类型
        return excelService.importExcel(file, "user_import");
    }
    
    /**
     * 示例2: 带回调的导入(批量入库)
     * 在导入过程中批量保存数据到数据库
     */
    public ExcelImportResult<Map<String, Object>> importUsersWithSave(MultipartFile file) {
        return excelService.importExcel(file, "user_import", 0, batchData -> {
            // 这里是批处理回调,每1000条数据回调一次
            log.info("批量保存用户数据, 本批次数量: {}", batchData.size());
            
            // 在这里调用Mapper批量插入数据
            // 例如: userMapper.batchInsert(convertToUserList(batchData));
            
            for (Map<String, Object> row : batchData) {
                // 处理每行数据
                String username = (String) row.get("username");
                String email = (String) row.get("email");
                log.debug("处理用户: {}, 邮箱: {}", username, email);
            }
        });
    }
    
    /**
     * 示例3: 导出数据到响应流
     * 从数据库查询数据并导出为Excel
     */
    public void exportUsers(HttpServletResponse response) {
        // 模拟从数据库查询数据
        List<Map<String, Object>> userData = new ArrayList<>();
        
        Map<String, Object> user1 = new HashMap<>();
        user1.put("username", "zhangsan");
        user1.put("nickname", "张三");
        user1.put("email", "zhangsan@example.com");
        user1.put("phone", "13800138001");
        user1.put("status", "启用");
        userData.add(user1);
        
        Map<String, Object> user2 = new HashMap<>();
        user2.put("username", "lisi");
        user2.put("nickname", "李四");
        user2.put("email", "lisi@example.com");
        user2.put("phone", "13800138002");
        user2.put("status", "启用");
        userData.add(user2);
        
        // 构建导出请求
        ExcelExportRequest request = new ExcelExportRequest();
        request.setTaskType("user_import"); // 使用同样的配置类型
        request.setFileName("用户数据导出");
        request.setMapData(userData);
        
        // 执行导出
        excelService.exportExcel(response, request);
    }
    
    /**
     * 示例4: 导出时排除某些字段
     */
    public void exportUsersWithExclude(HttpServletResponse response) {
        List<Map<String, Object>> userData = queryUsersFromDatabase();
        
        ExcelExportRequest request = new ExcelExportRequest();
        request.setTaskType("user_import");
        request.setFileName("用户数据(脱敏)");
        request.setMapData(userData);
        
        // 排除敏感字段
        List<String> excludeFields = new ArrayList<>();
        excludeFields.add("phone"); // 不导出手机号
        excludeFields.add("email"); // 不导出邮箱
        request.setExcludeFields(excludeFields);
        
        excelService.exportExcel(response, request);
    }
    
    /**
     * 示例5: 只导出指定字段
     */
    public void exportUsersWithInclude(HttpServletResponse response) {
        List<Map<String, Object>> userData = queryUsersFromDatabase();
        
        ExcelExportRequest request = new ExcelExportRequest();
        request.setTaskType("user_import");
        request.setFileName("用户基础信息");
        request.setMapData(userData);
        
        // 只导出指定字段
        List<String> includeFields = new ArrayList<>();
        includeFields.add("username");
        includeFields.add("nickname");
        includeFields.add("status");
        request.setIncludeFields(includeFields);
        
        excelService.exportExcel(response, request);
    }
    
    /**
     * 示例6: 导出失败数据
     * 导入后,如果有失败的数据,可以导出让用户修正后重新上传
     */
    public void exportFailedData(HttpServletResponse response, 
                                  ExcelImportResult<?> importResult) {
        if (importResult.getFailedRows() > 0) {
            excelService.exportFailedData(response, importResult, "user_import");
        }
    }
    
    /**
     * 示例7: 下载导入模板
     */
    public void downloadTemplate(HttpServletResponse response) {
        excelService.downloadTemplate(response, "user_import");
    }
    
    /**
     * 示例8: 小说数据导入
     */
    public ExcelImportResult<Map<String, Object>> importNovels(MultipartFile file) {
        return excelService.importExcel(file, "novel_import", 0, batchData -> {
            log.info("批量保存小说数据, 本批次数量: {}", batchData.size());
            // novelMapper.batchInsert(convertToNovelList(batchData));
        });
    }
    
    /**
     * 示例9: 章节数据导入
     */
    public ExcelImportResult<Map<String, Object>> importChapters(MultipartFile file) {
        return excelService.importExcel(file, "chapter_import", 0, batchData -> {
            log.info("批量保存章节数据, 本批次数量: {}", batchData.size());
            // chapterMapper.batchInsert(convertToChapterList(batchData));
        });
    }
    
    // 模拟数据库查询
    private List<Map<String, Object>> queryUsersFromDatabase() {
        List<Map<String, Object>> result = new ArrayList<>();
        // 实际项目中这里会调用Mapper查询数据库
        return result;
    }
}
