package com.app.novelvoice.service;

import com.app.novelvoice.dto.PrivateMessageRequest;
import com.app.novelvoice.entity.Message;
import com.app.novelvoice.vo.ConversationVO;
import com.app.novelvoice.vo.MessageVO;
import com.app.novelvoice.vo.UserVO;

import java.util.List;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 保存群聊消息
     */
    void saveMessage(Message message);

    /**
     * 获取群聊历史消息
     */
    List<MessageVO> getHistoryMessages();
    
    /**
     * 发送私聊消息
     */
    MessageVO sendPrivateMessage(PrivateMessageRequest request);
    
    /**
     * 获取私聊消息历史
     */
    List<MessageVO> getPrivateMessages(Long userId, Long targetUserId, int limit);
    
    /**
     * 获取用户的会话列表
     */
    List<ConversationVO> getConversations(Long userId);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long userId, Long senderId);
    
    /**
     * 获取在线用户列表
     */
    List<UserVO> getOnlineUsers();
    
    /**
     * 获取所有用户列表（用于私聊选择）
     */
    List<UserVO> getAllUsers(Long excludeUserId);
}
