package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ChapterVO {
    private Long id;
    private Long novelId;
    private String title;
    private String content;
    private Integer chapterNo;
    private Date createTime;
}
