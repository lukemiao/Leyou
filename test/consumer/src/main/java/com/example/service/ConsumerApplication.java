package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication
//@EnableDiscoveryClient//@EnableEurekaClient
//@EnableCircuitBreaker // 开启熔断

@SpringCloudApplication//组合注解 相当于 上面三个

@EnableFeignClients //启用feign
public class ConsumerApplication {

//    @Bean
////    @LoadBalanced//使用负载均衡机制
////    public RestTemplate restTemplate(){
////        return new RestTemplate();
////    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
