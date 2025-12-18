package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message message);

    List<Message> selectAll();
}
