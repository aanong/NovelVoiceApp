package com.app.novelvoice.entity;

import lombok.Data;

@Data
public class Novel {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String coverUrl;
}
