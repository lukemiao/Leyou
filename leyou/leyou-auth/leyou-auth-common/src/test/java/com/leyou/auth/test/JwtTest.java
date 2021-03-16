package com.leyou.auth.test;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "E:\\javaitem\\application\\leyou\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\javaitem\\application\\leyou\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU5NTkwODE3NH0.CtNsxJh8JBUV8gTzBJ9oLMUyaMl4DaXLkd6tJRw4mr0XHX4wk0Eoz6WWj465gfzlK7GkU9qnESh7HImY8Lkq5gY6na1oGsmPTC9dkl0sbuF7Gm_D1hUSRRaE1voMFfuF85oUVOjB2w6oL-59UMF4IhcfR5f1_hMXzkHHIf0rzAY";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}