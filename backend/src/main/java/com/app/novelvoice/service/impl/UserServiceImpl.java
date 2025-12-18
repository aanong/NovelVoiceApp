package com.app.novelvoice.service.impl;

import com.app.novelvoice.common.BusinessException;
import com.app.novelvoice.entity.User;
import com.app.novelvoice.mapper.UserMapper;
import com.app.novelvoice.service.UserService;
import com.app.novelvoice.vo.UserVO;
import com.app.novelvoice.util.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserVO login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }
        throw new BusinessException(401, "Invalid username or password");
    }

    @Override
    public UserVO register(String username, String password, String nickname) {
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            throw new BusinessException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.hash(password));
        user.setNickname(nickname);
        user.setCreateTime(new Date());
        userMapper.insert(user);

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
