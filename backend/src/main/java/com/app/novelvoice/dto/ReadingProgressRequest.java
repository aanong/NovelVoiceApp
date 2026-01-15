package com.app.novelvoice.dto;

import lombok.Data;

/**
 * 阅读进度请求对象
 */
@Data
public class ReadingProgressRequest {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 小说ID
     */
    private Long novelId;
    
    /**
     * 当前章节ID
     */
    private Long chapterId;
    
    /**
     * 章节序号
     */
    private Integer chapterNo;
    
    /**
     * 滚动位置（像素）
     */
    private Integer scrollPosition;
    
    /**
     * TTS朗读位置（字符索引）
     */
    private Integer ttsPosition;
}
