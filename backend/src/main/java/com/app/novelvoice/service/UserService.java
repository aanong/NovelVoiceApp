package com.app.novelvoice.service;

import com.app.novelvoice.vo.UserVO;

public interface UserService {
    UserVO login(String username, String password);

    UserVO register(String username, String password, String nickname);
}
