package com.app.novelvoice.controller;

import com.app.novelvoice.dto.ReadingProgressRequest;
import com.app.novelvoice.service.ReadingProgressService;
import com.app.novelvoice.vo.ReadingProgressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅读进度控制器
 */
@RestController
@RequestMapping("/api/reading-progress")
public class ReadingProgressController {

    @Autowired
    private ReadingProgressService readingProgressService;

    /**
     * 保存阅读进度
     */
    @PostMapping("/save")
    public void saveProgress(@RequestBody ReadingProgressRequest request) {
        readingProgressService.saveProgress(request);
    }

    /**
     * 获取指定小说的阅读进度
     */
    @GetMapping("/{userId}/{novelId}")
    public ReadingProgressVO getProgress(@PathVariable Long userId, @PathVariable Long novelId) {
        return readingProgressService.getProgress(userId, novelId);
    }

    /**
     * 获取用户的阅读历史
     */
    @GetMapping("/history/{userId}")
    public List<ReadingProgressVO> getReadingHistory(@PathVariable Long userId) {
        return readingProgressService.getReadingHistory(userId);
    }
}
