package com.app.novelvoice.controller;

import com.app.novelvoice.service.ChatService;
import com.app.novelvoice.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history")
    public List<MessageVO> getHistory() {
        return chatService.getHistoryMessages();
    }
}
