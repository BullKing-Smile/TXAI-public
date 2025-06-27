package com.txai.nacosclustertesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class NacosClusterTestingApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosClusterTestingApplication.class, args);
    }
}
