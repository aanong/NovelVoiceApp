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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserVO login(String username, String password) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(400, "密码不能为空");
        }
        
        User user = userMapper.selectByUsername(username.trim());
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }
        
        // 生成 Token
        String token = UUID.randomUUID().toString().replace("-", "");
        user.setToken(token);
        user.setLastLoginTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.update(user);
        
        return convertToVO(user);
    }

    @Override
    public UserVO register(String username, String password, String nickname) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = username; // 默认昵称为用户名
        }
        
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(username.trim());
        if (existing != null) {
            throw new BusinessException(400, "用户名已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordUtil.hash(password));
        user.setNickname(nickname.trim());
        user.setCreateTime(new Date());
        userMapper.insert(user);

        return convertToVO(user);
    }

    @Override
    public UserVO getUserByToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        User user = userMapper.selectByToken(token);
        if (user == null) {
            return null;
        }
        return convertToVO(user);
    }

    @Override
    public void logout(Long userId) {
        userMapper.updateToken(userId, null);
    }

    @Override
    public UserVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        return convertToVO(user);
    }

    @Override
    public List<UserVO> getAllUsers() {
        List<User> users = userMapper.selectAll();
        return users.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    /**
     * 将实体转换为 VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        // 简化的在线判断
        if (user.getLastLoginTime() != null) {
            long diff = System.currentTimeMillis() - user.getLastLoginTime().getTime();
            vo.setOnline(diff < 10 * 60 * 1000);
        } else {
            vo.setOnline(false);
        }
        return vo;
    }
}
