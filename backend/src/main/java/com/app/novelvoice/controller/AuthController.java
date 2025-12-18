package com.app.novelvoice.controller;

import com.app.novelvoice.common.Result;
import com.app.novelvoice.dto.LoginRequest;
import com.app.novelvoice.dto.RegisterRequest;
import com.app.novelvoice.entity.User;
import com.app.novelvoice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        User user = userService.login(username, password);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error(401, "Invalid credentials");
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String nickname = request.getNickname();
        try {
            User user = userService.register(username, password, nickname);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
