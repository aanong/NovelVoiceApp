package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会话 Mapper 接口
 */
@Mapper
public interface ConversationMapper {
    
    /**
     * 插入会话
     */
    int insert(Conversation conversation);
    
    /**
     * 更新会话
     */
    int update(Conversation conversation);
    
    /**
     * 根据ID查询会话
     */
    Conversation selectById(@Param("id") Long id);
    
    /**
     * 根据两个用户ID查询会话
     */
    Conversation selectByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    
    /**
     * 查询用户的所有会话
     */
    List<Conversation> selectByUserId(@Param("userId") Long userId);
}
