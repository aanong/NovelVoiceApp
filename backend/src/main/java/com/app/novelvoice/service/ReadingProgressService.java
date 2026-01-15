package com.app.novelvoice.service;

import com.app.novelvoice.dto.ReadingProgressRequest;
import com.app.novelvoice.vo.ReadingProgressVO;

import java.util.List;

/**
 * 阅读进度服务接口
 */
public interface ReadingProgressService {
    
    /**
     * 保存或更新阅读进度
     */
    void saveProgress(ReadingProgressRequest request);
    
    /**
     * 获取用户在某本小说的阅读进度
     */
    ReadingProgressVO getProgress(Long userId, Long novelId);
    
    /**
     * 获取用户的阅读历史列表
     */
    List<ReadingProgressVO> getReadingHistory(Long userId);
}
