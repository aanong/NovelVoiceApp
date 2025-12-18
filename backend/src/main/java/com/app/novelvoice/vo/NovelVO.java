package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

@Data
public class NovelVO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String coverUrl;
    private Date createTime;
}
