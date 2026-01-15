package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

/**
 * 阅读进度视图对象
 */
@Data
public class ReadingProgressVO {
    private Long id;
    private Long userId;
    private Long novelId;
    private String novelTitle;
    private Long chapterId;
    private String chapterTitle;
    private Integer chapterNo;
    private Integer scrollPosition;
    private Integer ttsPosition;
    private Date lastReadTime;
}
