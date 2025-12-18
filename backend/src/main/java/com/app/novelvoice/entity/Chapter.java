package com.app.novelvoice.entity;

import lombok.Data;

@Data
public class Chapter {
    private Long id;
    private Long novelId;
    private String title;
    private String content;
    private Integer chapterNo;
}
