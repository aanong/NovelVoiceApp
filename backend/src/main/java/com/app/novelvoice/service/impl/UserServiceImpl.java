package com.app.novelvoice.service.impl;

import com.app.novelvoice.entity.User;
import com.app.novelvoice.mapper.UserMapper;
import com.app.novelvoice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.novelvoice.util.PasswordUtil;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            return user;
        }
        return null; // Or throw exception
    }

    @Override
    public User register(String username, String password, String nickname) {
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setNickname(nickname);
        user.setCreatedAt(new Date());
        userMapper.insert(user);
        return user;
    }
}
