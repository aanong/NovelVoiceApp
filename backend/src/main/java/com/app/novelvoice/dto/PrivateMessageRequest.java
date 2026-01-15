package com.app.novelvoice.dto;

import lombok.Data;

/**
 * 私聊消息请求对象
 */
@Data
public class PrivateMessageRequest {
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：0-文本, 1-图片, 2-表情, 3-文件
     */
    private Integer type;
    
    /**
     * 文件URL（图片/文件类型时使用）
     */
    private String fileUrl;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
}
