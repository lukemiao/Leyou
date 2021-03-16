package com.example.test.controller;

import com.example.test.pojo.User;
import com.example.test.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("{id}")
    public User selectUserById(@PathVariable("id") Integer id) {
        return this.userService.select(id);
    }
}
