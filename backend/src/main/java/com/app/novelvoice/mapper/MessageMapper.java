package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息 Mapper 接口
 */
@Mapper
public interface MessageMapper {
    
    /**
     * 插入消息
     */
    int insert(Message message);

    /**
     * 查询所有群聊消息
     */
    List<Message> selectAll();
    
    /**
     * 查询群聊历史消息（带数量限制）
     */
    List<Message> selectRecent(@Param("limit") int limit);
    
    /**
     * 查询私聊消息（根据会话ID）
     */
    List<Message> selectByConversationId(@Param("conversationId") Long conversationId, @Param("limit") int limit);
    
    /**
     * 查询两个用户之间的私聊消息
     */
    List<Message> selectByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id, @Param("limit") int limit);
    
    /**
     * 标记消息为已读
     */
    int markAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);
    
    /**
     * 标记会话中的所有消息为已读
     */
    int markConversationAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
    
    /**
     * 查询未读消息数量
     */
    int countUnread(@Param("userId") Long userId);
    
    /**
     * 查询与某用户的未读消息数量
     */
    int countUnreadBySender(@Param("userId") Long userId, @Param("senderId") Long senderId);
}
