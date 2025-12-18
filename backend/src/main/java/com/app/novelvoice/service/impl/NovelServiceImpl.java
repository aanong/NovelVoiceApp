package com.app.novelvoice.service.impl;

import com.app.novelvoice.entity.Chapter;
import com.app.novelvoice.entity.Novel;
import com.app.novelvoice.mapper.ChapterMapper;
import com.app.novelvoice.mapper.NovelMapper;
import com.app.novelvoice.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NovelServiceImpl implements NovelService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Override
    public List<Novel> getNovelList() {
        return novelMapper.selectAll();
    }

    @Override
    public Novel getNovelById(Long id) {
        return novelMapper.selectById(id);
    }

    @Override
    public List<Chapter> getChapters(Long novelId) {
        // Returns chapters without big content loaded typically, but keeping simple
        return chapterMapper.selectByNovelId(novelId);
    }

    @Override
    public Chapter getChapterContent(Long id) {
        return chapterMapper.selectById(id);
    }
}
