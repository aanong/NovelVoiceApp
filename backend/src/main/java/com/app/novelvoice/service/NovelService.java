package com.app.novelvoice.service;

import com.app.novelvoice.vo.ChapterVO;
import com.app.novelvoice.vo.NovelVO;
import java.util.List;

public interface NovelService {
    List<NovelVO> getNovelList();

    NovelVO getNovelById(Long id);

    List<ChapterVO> getChapters(Long novelId);

    ChapterVO getChapterContent(Long id);
}
