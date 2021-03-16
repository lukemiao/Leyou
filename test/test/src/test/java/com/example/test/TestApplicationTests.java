package com.example.test;

import com.example.test.pojo.User;
import com.example.test.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestApplicationTests {

    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
        User select = userService.select(1);
        System.out.println(select);
    }

}
