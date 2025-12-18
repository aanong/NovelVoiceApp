package com.app.novelvoice.service;

import com.app.novelvoice.entity.Message;
import java.util.List;

public interface ChatService {
    void saveMessage(Message message);

    List<Message> getHistoryMessages();
}
