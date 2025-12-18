package com.app.novelvoice.controller;

import com.app.novelvoice.common.Result;
import com.app.novelvoice.dto.LoginRequest;
import com.app.novelvoice.dto.RegisterRequest;
import com.app.novelvoice.entity.User;
import com.app.novelvoice.service.UserService;
import com.app.novelvoice.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error(401, "Invalid credentials");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String nickname = request.getNickname();
        try {
            User user = userService.register(username, password, nickname);
            if (user == null) {
                return Result.error(500, "Registration failed");
            }
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
