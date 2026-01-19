package com.app.novelvoice.service.impl;

import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.entity.Chapter;
import com.app.novelvoice.entity.Novel;
import com.app.novelvoice.mapper.ChapterMapper;
import com.app.novelvoice.mapper.NovelMapper;
import com.app.novelvoice.service.NovelService;
import com.app.novelvoice.vo.ChapterVO;
import com.app.novelvoice.vo.NovelVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NovelServiceImpl implements NovelService {

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Override
    public List<NovelVO> getNovelList() {
        List<Novel> list = novelMapper.selectAll();
        return list.stream().map(novel -> {
            NovelVO vo = new NovelVO();
            BeanUtils.copyProperties(novel, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public NovelVO getNovelById(Long id) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            throw new BusinessException(404, "Novel not found");
        }
        NovelVO vo = new NovelVO();
        BeanUtils.copyProperties(novel, vo);
        return vo;
    }

    @Override
    public List<ChapterVO> getChapters(Long novelId) {
        List<Chapter> list = chapterMapper.selectByNovelId(novelId);
        return list.stream().map(chapter -> {
            ChapterVO vo = new ChapterVO();
            BeanUtils.copyProperties(chapter, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public ChapterVO getChapterContent(Long id) {
        Chapter chapter = chapterMapper.selectById(id);
        if (chapter == null) {
            throw new BusinessException(404, "Chapter not found");
        }
        ChapterVO vo = new ChapterVO();
        BeanUtils.copyProperties(chapter, vo);
        return vo;
    }

    @Override
    public void deleteNovel(Long id) {
        chapterMapper.deleteByNovelId(id);
        novelMapper.deleteById(id);
    }
}
