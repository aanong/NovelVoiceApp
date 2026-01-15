package com.app.novelvoice.service.impl;

import com.app.novelvoice.dto.PrivateMessageRequest;
import com.app.novelvoice.entity.Conversation;
import com.app.novelvoice.entity.Message;
import com.app.novelvoice.entity.User;
import com.app.novelvoice.mapper.ConversationMapper;
import com.app.novelvoice.mapper.MessageMapper;
import com.app.novelvoice.mapper.UserMapper;
import com.app.novelvoice.service.ChatService;
import com.app.novelvoice.vo.ConversationVO;
import com.app.novelvoice.vo.MessageVO;
import com.app.novelvoice.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private ConversationMapper conversationMapper;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public void saveMessage(Message message) {
        // 群聊消息，receiverId 为空
        message.setReceiverId(null);
        message.setConversationId(null);
        message.setIsRead(false);
        message.setCreateTime(new Date());
        messageMapper.insert(message);
    }

    @Override
    public List<MessageVO> getHistoryMessages() {
        List<Message> list = messageMapper.selectAll();
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageVO sendPrivateMessage(PrivateMessageRequest request) {
        // 获取或创建会话
        Long conversationId = getOrCreateConversation(request.getSenderId(), request.getReceiverId());
        
        // 创建消息
        Message message = new Message();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setConversationId(conversationId);
        message.setContent(request.getContent());
        message.setType(request.getType() != null ? request.getType() : 0);
        message.setFileUrl(request.getFileUrl());
        message.setFileName(request.getFileName());
        message.setFileSize(request.getFileSize());
        message.setIsRead(false);
        message.setCreateTime(new Date());
        
        messageMapper.insert(message);
        
        // 更新会话最后消息
        Conversation conversation = conversationMapper.selectById(conversationId);
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageTime(new Date());
        conversation.setUpdateTime(new Date());
        conversationMapper.update(conversation);
        
        return convertToVO(message);
    }

    @Override
    public List<MessageVO> getPrivateMessages(Long userId, Long targetUserId, int limit) {
        List<Message> messages = messageMapper.selectByUsers(userId, targetUserId, limit);
        return messages.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<ConversationVO> getConversations(Long userId) {
        List<Conversation> conversations = conversationMapper.selectByUserId(userId);
        List<ConversationVO> voList = new ArrayList<>();
        
        for (Conversation conv : conversations) {
            ConversationVO vo = new ConversationVO();
            vo.setId(conv.getId());
            
            // 确定对方用户ID
            Long targetUserId = conv.getUser1Id().equals(userId) ? conv.getUser2Id() : conv.getUser1Id();
            vo.setTargetUserId(targetUserId);
            
            // 获取对方用户信息
            User targetUser = userMapper.selectById(targetUserId);
            if (targetUser != null) {
                vo.setTargetNickname(targetUser.getNickname());
                vo.setTargetAvatar(targetUser.getAvatar());
            }
            
            // 获取最后一条消息
            if (conv.getLastMessageId() != null) {
                // 简化处理，直接使用会话的最后消息时间
                vo.setLastMessageTime(conv.getLastMessageTime());
            }
            
            // 获取未读消息数
            int unreadCount = messageMapper.countUnreadBySender(userId, targetUserId);
            vo.setUnreadCount(unreadCount);
            
            voList.add(vo);
        }
        
        return voList;
    }

    @Override
    public void markAsRead(Long userId, Long senderId) {
        messageMapper.markAsRead(userId, senderId);
    }

    @Override
    public List<UserVO> getOnlineUsers() {
        // 简化实现：返回最近活跃的用户
        // 实际生产环境中应该使用 Redis 或内存缓存维护在线用户列表
        List<User> users = userMapper.selectAll();
        return users.stream().map(this::convertUserToVO).collect(Collectors.toList());
    }

    @Override
    public List<UserVO> getAllUsers(Long excludeUserId) {
        List<User> users = userMapper.selectAll();
        return users.stream()
                .filter(u -> !u.getId().equals(excludeUserId))
                .map(this::convertUserToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取或创建会话
     */
    private Long getOrCreateConversation(Long user1Id, Long user2Id) {
        Conversation existing = conversationMapper.selectByUsers(user1Id, user2Id);
        if (existing != null) {
            return existing.getId();
        }
        
        // 创建新会话，确保 user1Id < user2Id 以保证唯一性
        Conversation conversation = new Conversation();
        if (user1Id < user2Id) {
            conversation.setUser1Id(user1Id);
            conversation.setUser2Id(user2Id);
        } else {
            conversation.setUser1Id(user2Id);
            conversation.setUser2Id(user1Id);
        }
        conversation.setCreateTime(new Date());
        conversationMapper.insert(conversation);
        
        return conversation.getId();
    }
    
    /**
     * 将消息实体转换为 VO
     */
    private MessageVO convertToVO(Message msg) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(msg, vo);
        
        // 获取发送者信息
        User sender = userMapper.selectById(msg.getSenderId());
        if (sender != null) {
            vo.setSenderNickname(sender.getNickname());
            vo.setSenderAvatar(sender.getAvatar());
        }
        
        return vo;
    }
    
    /**
     * 将用户实体转换为 VO
     */
    private UserVO convertUserToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        // 简化的在线判断：最近10分钟登录过视为在线
        if (user.getLastLoginTime() != null) {
            long diff = System.currentTimeMillis() - user.getLastLoginTime().getTime();
            vo.setOnline(diff < 10 * 60 * 1000);
        } else {
            vo.setOnline(false);
        }
        return vo;
    }
}
