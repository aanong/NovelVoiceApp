package com.app.novelvoice.service;

import com.app.novelvoice.entity.Message;
import com.app.novelvoice.vo.MessageVO;
import java.util.List;

public interface ChatService {
    void saveMessage(Message message);

    List<MessageVO> getHistoryMessages();
}
