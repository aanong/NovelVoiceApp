package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {
    private Long id;
    private Long senderId;
    private String content;
    private Integer type; // 0: text, 1: image
}
