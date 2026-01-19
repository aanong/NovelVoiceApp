package com.app.novelvoice.controller;

import com.app.novelvoice.service.NovelService;
import com.app.novelvoice.vo.ChapterVO;
import com.app.novelvoice.vo.NovelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    @Autowired
    private NovelService novelService;

    @GetMapping("/list")
    public List<NovelVO> getList() {
        return novelService.getNovelList();
    }

    @GetMapping("/{id}")
    public NovelVO getDetail(@PathVariable Long id) {
        return novelService.getNovelById(id);
    }

    @GetMapping("/{id}/chapters")
    public List<ChapterVO> getChapters(@PathVariable Long id) {
        return novelService.getChapters(id);
    }

    @GetMapping("/chapters/{id}")
    public ChapterVO getChapterContent(@PathVariable Long id) {
        return novelService.getChapterContent(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        novelService.deleteNovel(id);
    }
}
