package com.example.service.client;

import com.example.service.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient{
    @Override
    public User selectUserById(Integer id) {
        User user = new User();
        user.setUsername("服务器正忙，请稍后重试");
        return user;

    }
}
