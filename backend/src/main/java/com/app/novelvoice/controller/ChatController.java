package com.app.novelvoice.controller;

import com.app.novelvoice.dto.PrivateMessageRequest;
import com.app.novelvoice.service.ChatService;
import com.app.novelvoice.vo.ConversationVO;
import com.app.novelvoice.vo.MessageVO;
import com.app.novelvoice.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 获取群聊历史消息
     */
    @GetMapping("/history")
    public List<MessageVO> getHistory() {
        return chatService.getHistoryMessages();
    }
    
    /**
     * 发送私聊消息
     */
    @PostMapping("/private/send")
    public MessageVO sendPrivateMessage(@RequestBody PrivateMessageRequest request) {
        return chatService.sendPrivateMessage(request);
    }
    
    /**
     * 获取私聊消息历史
     */
    @GetMapping("/private/{userId}/{targetUserId}")
    public List<MessageVO> getPrivateMessages(
            @PathVariable Long userId,
            @PathVariable Long targetUserId,
            @RequestParam(defaultValue = "100") int limit) {
        return chatService.getPrivateMessages(userId, targetUserId, limit);
    }
    
    /**
     * 获取用户的会话列表
     */
    @GetMapping("/conversations/{userId}")
    public List<ConversationVO> getConversations(@PathVariable Long userId) {
        return chatService.getConversations(userId);
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/read/{userId}/{senderId}")
    public void markAsRead(@PathVariable Long userId, @PathVariable Long senderId) {
        chatService.markAsRead(userId, senderId);
    }
    
    /**
     * 获取所有用户列表（用于私聊选择）
     */
    @GetMapping("/users/{excludeUserId}")
    public List<UserVO> getAllUsers(@PathVariable Long excludeUserId) {
        return chatService.getAllUsers(excludeUserId);
    }
}
