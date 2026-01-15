package com.app.novelvoice.service;

import com.app.novelvoice.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    UserVO login(String username, String password);

    /**
     * 用户注册
     */
    UserVO register(String username, String password, String nickname);
    
    /**
     * 根据 Token 获取用户信息
     */
    UserVO getUserByToken(String token);
    
    /**
     * 用户登出
     */
    void logout(Long userId);
    
    /**
     * 根据ID获取用户信息
     */
    UserVO getUserById(Long userId);
    
    /**
     * 获取所有用户列表
     */
    List<UserVO> getAllUsers();
}
