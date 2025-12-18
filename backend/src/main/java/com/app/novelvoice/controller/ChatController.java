package com.app.novelvoice.controller;

import com.app.novelvoice.common.Result;
import com.app.novelvoice.entity.Message;
import com.app.novelvoice.service.ChatService;
import com.app.novelvoice.vo.MessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history")
    public Result<List<MessageVO>> getHistory() {
        List<Message> messages = chatService.getHistoryMessages();
        if (messages == null) {
            return Result.success(Collections.emptyList());
        }
        List<MessageVO> voList = messages.stream().map(msg -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(msg, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }
}
