package com.example.test.service;

import com.example.test.mapper.UserMapper;
import com.example.test.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User select(Integer id){
        return userMapper.selectByPrimaryKey(id);
    }
}
