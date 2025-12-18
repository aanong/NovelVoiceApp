package com.app.novelvoice.service.impl;

import com.app.novelvoice.entity.Message;
import com.app.novelvoice.mapper.MessageMapper;
import com.app.novelvoice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void saveMessage(Message message) {
        messageMapper.insert(message);
    }

    @Override
    public List<Message> getHistoryMessages() {
        return messageMapper.selectAll();
    }
}
