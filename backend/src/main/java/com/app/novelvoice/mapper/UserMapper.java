package com.app.novelvoice.mapper;

import com.app.novelvoice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper {
    int insert(User user);

    int update(User user);

    User selectByUsername(@Param("username") String username);

    User selectById(@Param("id") Long id);
    
    User selectByToken(@Param("token") String token);
    
    /**
     * 查询所有用户列表（用于私聊用户选择）
     */
    List<User> selectAll();
    
    /**
     * 更新用户Token
     */
    int updateToken(@Param("id") Long id, @Param("token") String token);
}
