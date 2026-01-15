package com.app.novelvoice.vo;

import lombok.Data;
import java.util.Date;

/**
 * 会话视图对象
 */
@Data
public class ConversationVO {
    private Long id;
    
    /**
     * 对方用户信息
     */
    private Long targetUserId;
    private String targetNickname;
    private String targetAvatar;
    private Boolean targetOnline;
    
    /**
     * 最后一条消息
     */
    private String lastMessageContent;
    private Integer lastMessageType;
    private Date lastMessageTime;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
}
