package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

@Data
public class MessageVO {
    private Long id;
    private Long senderId;
    private String content;
    private Integer type;
    private Date createTime;
}
