package com.app.novelvoice.controller;

import com.app.novelvoice.dto.LoginRequest;
import com.app.novelvoice.dto.RegisterRequest;
import com.app.novelvoice.service.UserService;
import com.app.novelvoice.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public UserVO login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/register")
    public UserVO register(@RequestBody RegisterRequest request) {
        return userService.register(request.getUsername(), request.getPassword(), request.getNickname());
    }
}
