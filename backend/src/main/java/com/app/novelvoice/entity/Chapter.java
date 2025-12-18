package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Chapter extends BaseEntity {
    private Long id;
    private Long novelId;
    private String title;
    private String content;
    private Integer chapterNo;
}
