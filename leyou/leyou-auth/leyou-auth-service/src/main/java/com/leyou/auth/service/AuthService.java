package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserClient userClient;

    @Autowired
    JwtProperties jwtProperties;

    public String accredit(String username, String password) {
        //根据用户名和密码查询
        User user = userClient.queryUser(username, password);
        //判断user
        if (user == null) return null;
        //jwtUtils生成jwt类型的token
        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
