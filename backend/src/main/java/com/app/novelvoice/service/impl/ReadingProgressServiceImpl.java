package com.app.novelvoice.service.impl;

import com.app.novelvoice.dto.ReadingProgressRequest;
import com.app.novelvoice.entity.Chapter;
import com.app.novelvoice.entity.Novel;
import com.app.novelvoice.entity.ReadingProgress;
import com.app.novelvoice.mapper.ChapterMapper;
import com.app.novelvoice.mapper.NovelMapper;
import com.app.novelvoice.mapper.ReadingProgressMapper;
import com.app.novelvoice.service.ReadingProgressService;
import com.app.novelvoice.vo.ReadingProgressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阅读进度服务实现类
 */
@Service
public class ReadingProgressServiceImpl implements ReadingProgressService {

    @Autowired
    private ReadingProgressMapper readingProgressMapper;
    
    @Autowired
    private NovelMapper novelMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;

    @Override
    public void saveProgress(ReadingProgressRequest request) {
        ReadingProgress progress = new ReadingProgress();
        BeanUtils.copyProperties(request, progress);
        progress.setLastReadTime(new Date());
        
        // 使用 insertOrUpdate 实现保存或更新
        readingProgressMapper.insertOrUpdate(progress);
    }

    @Override
    public ReadingProgressVO getProgress(Long userId, Long novelId) {
        ReadingProgress progress = readingProgressMapper.selectByUserAndNovel(userId, novelId);
        if (progress == null) {
            return null;
        }
        return convertToVO(progress);
    }

    @Override
    public List<ReadingProgressVO> getReadingHistory(Long userId) {
        List<ReadingProgress> progressList = readingProgressMapper.selectByUserId(userId);
        List<ReadingProgressVO> voList = new ArrayList<>();
        for (ReadingProgress progress : progressList) {
            voList.add(convertToVO(progress));
        }
        return voList;
    }
    
    /**
     * 将实体转换为 VO
     */
    private ReadingProgressVO convertToVO(ReadingProgress progress) {
        ReadingProgressVO vo = new ReadingProgressVO();
        BeanUtils.copyProperties(progress, vo);
        
        // 填充小说标题
        Novel novel = novelMapper.selectById(progress.getNovelId());
        if (novel != null) {
            vo.setNovelTitle(novel.getTitle());
        }
        
        // 填充章节标题
        Chapter chapter = chapterMapper.selectById(progress.getChapterId());
        if (chapter != null) {
            vo.setChapterTitle(chapter.getTitle());
        }
        
        return vo;
    }
}
