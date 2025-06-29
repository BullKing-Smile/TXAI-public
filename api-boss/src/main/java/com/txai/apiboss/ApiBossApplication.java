package com.txai.apiboss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
public class ApiBossApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiBossApplication.class, args);
    }
}
