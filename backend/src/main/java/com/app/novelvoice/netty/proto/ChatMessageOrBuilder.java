package com.app.novelvoice.netty.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

/**
 * ChatMessage 消息接口
 * 定义所有字段的 getter 方法
 */
public interface ChatMessageOrBuilder extends MessageOrBuilder {
    
    /**
     * 发送者ID
     */
    long getSenderId();

    /**
     * 接收者ID（0表示群聊）
     */
    long getReceiverId();

    /**
     * 消息内容
     */
    String getContent();
    ByteString getContentBytes();

    /**
     * 消息类型：0-文本, 1-图片, 2-表情, 3-文件
     */
    int getType();

    /**
     * 时间戳
     */
    String getTimestamp();
    ByteString getTimestampBytes();

    /**
     * 文件URL
     */
    String getFileUrl();
    ByteString getFileUrlBytes();

    /**
     * 文件名
     */
    String getFileName();
    ByteString getFileNameBytes();

    /**
     * 文件大小（字节）
     */
    long getFileSize();

    /**
     * 发送者昵称
     */
    String getSenderNickname();
    ByteString getSenderNicknameBytes();

    /**
     * 发送者头像URL
     */
    String getSenderAvatar();
    ByteString getSenderAvatarBytes();
}
