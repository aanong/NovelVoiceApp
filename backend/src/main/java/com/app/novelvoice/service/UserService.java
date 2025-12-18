package com.app.novelvoice.service;

import com.app.novelvoice.entity.User;

public interface UserService {
    User login(String username, String password);

    User register(String username, String password, String nickname);
}
