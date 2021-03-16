package com.example.service.controller;

import com.example.service.client.UserClient;
import com.example.service.pojo.User;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("consumer/user")

//@DefaultProperties(defaultFallback = "Fallback") //定义全局的熔断方法   注意返回值 String
public class UserController {

//    @Autowired
//    RestTemplate restTemplate;

//    @Autowired
//    DiscoveryClient discoveryClient;//包含了拉去的所有服务信息

    @Autowired
    UserClient userClient;

    @GetMapping
//    @HystrixCommand/*(fallbackMethod = "selectUserByIdFallback")*/ // 申明熔断的方法
    public User selectUserById(@RequestParam("id") Integer id) {
//        if (id == 1) {
//            throw new RuntimeException();
//        }
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-provider");
//        ServiceInstance instance = instances.get(0);
//        String host = instance.getHost();
//        int port = instance.getPort();
//        return restTemplate.getForObject("http://"+ host +":"+ port +"/user/"+id,User.class);
//        return restTemplate.getForObject("http://localhost:8080/user/"+id,User.class);
//        return restTemplate.getForObject("http://service-provider/user/" + id, String.class);

        return userClient.selectUserById(id);
    }

    //    public String selectUserByIdFallback(Integer id){
//      return "服务繁忙，请稍后再试!";
//    }
//    public String Fallback() {
//        return "服务繁忙，请稍后再试!";
//    }

}
