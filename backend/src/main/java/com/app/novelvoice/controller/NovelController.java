package com.app.novelvoice.controller;

import com.app.novelvoice.common.Result;
import com.app.novelvoice.entity.Chapter;
import com.app.novelvoice.entity.Novel;
import com.app.novelvoice.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @GetMapping("/list")
    public Result<List<Novel>> getList() {
        return Result.success(novelService.getNovelList());
    }

    @GetMapping("/{id}")
    public Result<Novel> getDetail(@PathVariable Long id) {
        return Result.success(novelService.getNovelById(id));
    }

    @GetMapping("/{id}/chapters")
    public Result<List<Chapter>> getChapters(@PathVariable Long id) {
        return Result.success(novelService.getChapters(id));
    }

    @GetMapping("/chapters/{id}")
    public Result<Chapter> getChapterContent(@PathVariable Long id) {
        return Result.success(novelService.getChapterContent(id));
    }
}
