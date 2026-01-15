package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 阅读进度实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReadingProgress extends BaseEntity {
    private Long id;
    
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
    
    /**
     * 最后阅读时间
     */
    private Date lastReadTime;
}
