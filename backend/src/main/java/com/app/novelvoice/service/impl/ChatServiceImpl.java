package com.app.novelvoice.service.impl;

import com.app.novelvoice.entity.Message;
import com.app.novelvoice.mapper.MessageMapper;
import com.app.novelvoice.service.ChatService;
import com.app.novelvoice.vo.MessageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void saveMessage(Message message) {
        messageMapper.insert(message);
    }

    @Override
    public List<MessageVO> getHistoryMessages() {
        List<Message> list = messageMapper.selectAll();
        return list.stream().map(msg -> {
            MessageVO vo = new MessageVO();
            BeanUtils.copyProperties(msg, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
