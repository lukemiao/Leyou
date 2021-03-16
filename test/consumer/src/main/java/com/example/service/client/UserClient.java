package com.example.service.client;

import com.example.service.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-provider",fallback = UserClientFallback.class ) //申明一个接口是feign接口，指定服务id
public interface UserClient {

    @GetMapping("user/{id}")
    public User selectUserById(@PathVariable("id") Integer id) ;
}
