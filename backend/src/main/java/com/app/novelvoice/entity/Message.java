package com.app.novelvoice.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Message {
    private Long id;
    private Long senderId;
    private String content;
    private Integer type; // 0: text, 1: image
    private Date createdAt;
}
