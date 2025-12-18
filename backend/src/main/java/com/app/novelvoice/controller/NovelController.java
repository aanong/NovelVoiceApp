package com.app.novelvoice.controller;

import com.app.novelvoice.common.Result;
import com.app.novelvoice.entity.Chapter;
import com.app.novelvoice.entity.Novel;
import com.app.novelvoice.service.NovelService;
import com.app.novelvoice.vo.ChapterVO;
import com.app.novelvoice.vo.NovelVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @GetMapping("/list")
    public Result<List<NovelVO>> getList() {
        List<Novel> list = novelService.getNovelList();
        if (list == null) {
            return Result.success(Collections.emptyList());
        }
        List<NovelVO> voList = list.stream().map(novel -> {
            NovelVO vo = new NovelVO();
            BeanUtils.copyProperties(novel, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/{id}")
    public Result<NovelVO> getDetail(@PathVariable Long id) {
        Novel novel = novelService.getNovelById(id);
        if (novel == null) {
            return Result.error(404, "Novel not found");
        }
        NovelVO vo = new NovelVO();
        BeanUtils.copyProperties(novel, vo);
        return Result.success(vo);
    }

    @GetMapping("/{id}/chapters")
    public Result<List<ChapterVO>> getChapters(@PathVariable Long id) {
        List<Chapter> chapters = novelService.getChapters(id);
        if (chapters == null) {
            return Result.success(Collections.emptyList());
        }
        List<ChapterVO> voList = chapters.stream().map(chapter -> {
            ChapterVO vo = new ChapterVO();
            BeanUtils.copyProperties(chapter, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/chapters/{id}")
    public Result<ChapterVO> getChapterContent(@PathVariable Long id) {
        Chapter chapter = novelService.getChapterContent(id);
        if (chapter == null) {
            return Result.error(404, "Chapter not found");
        }
        ChapterVO vo = new ChapterVO();
        BeanUtils.copyProperties(chapter, vo);
        return Result.success(vo);
    }
}
