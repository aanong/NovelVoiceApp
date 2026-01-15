package com.app.novelvoice.controller;

import com.app.novelvoice.dto.LoginRequest;
import com.app.novelvoice.dto.RegisterRequest;
import com.app.novelvoice.service.UserService;
import com.app.novelvoice.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public UserVO login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public UserVO register(@RequestBody RegisterRequest request) {
        return userService.register(request.getUsername(), request.getPassword(), request.getNickname());
    }
    
    /**
     * 根据 Token 获取用户信息
     */
    @GetMapping("/user")
    public UserVO getUserByToken(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return userService.getUserByToken(token);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout/{userId}")
    public void logout(@PathVariable Long userId) {
        userService.logout(userId);
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/user/{userId}")
    public UserVO getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }
}
