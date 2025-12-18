package com.app.novelvoice.service;

import com.app.novelvoice.entity.Chapter;
import com.app.novelvoice.entity.Novel;
import java.util.List;

public interface NovelService {
    List<Novel> getNovelList();

    Novel getNovelById(Long id);

    List<Chapter> getChapters(Long novelId);

    Chapter getChapterContent(Long id);
}
