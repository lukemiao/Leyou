package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:verify:";

    /**
     * 校验数据是否可用
     *
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        if (type == 1) {
            record.setUsername(data);
        } else if (type == 2) {
            record.setPhone(data);
        } else {
            return null;
        }
        return userMapper.selectCount(record) == 0;
    }

    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //生成验证码
        String code = NumberUtils.generateCode(6);

        //发送消息到rabbitMQ
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);

        amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE", "verifycode.sms", msg);

        //把验证码保存到redis中

        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {

        //通过手机的到内存中的 验证码
        String redisCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        //校验验证码
        if (!code.equals(redisCode)) {
            return;
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        user.setId(null);
        user.setCreated(new Date());
        userMapper.insertSelective(user);
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        if (user == null) {
            return null;
        } else {
            password = CodecUtils.md5Hex(password, user.getSalt());
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}
