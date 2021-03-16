package com.lkm.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer //启用 eureka 服务端
@SpringBootApplication
public class LkmEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LkmEurekaApplication.class, args);
    }

}
