package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User user);

    User selectByUsername(@Param("username") String username);

    User selectById(@Param("id") Long id);
}
