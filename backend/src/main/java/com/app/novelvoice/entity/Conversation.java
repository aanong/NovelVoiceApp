package com.app.novelvoice.entity;

import com.app.novelvoice.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 私聊会话实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Conversation extends BaseEntity {
    private Long id;
    
    /**
     * 用户1 ID
     */
    private Long user1Id;
    
    /**
     * 用户2 ID
     */
    private Long user2Id;
    
    /**
     * 最后消息ID
     */
    private Long lastMessageId;
    
    /**
     * 最后消息时间
     */
    private Date lastMessageTime;
}
