package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

/**
 * 消息视图对象
 */
@Data
public class MessageVO {
    private Long id;
    private Long senderId;
    private String senderNickname;
    private String senderAvatar;
    private Long receiverId;
    private Long conversationId;
    private String content;
    private Integer type;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Boolean isRead;
    private Date createTime;
}
