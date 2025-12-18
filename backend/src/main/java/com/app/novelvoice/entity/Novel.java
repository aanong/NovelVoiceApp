package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Novel extends BaseEntity {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String coverUrl;
}
