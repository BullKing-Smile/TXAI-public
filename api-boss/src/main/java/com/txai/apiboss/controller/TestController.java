package com.txai.apiboss.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TestController {

    @Value("${testConfig}")
    private String testConfig;


    @GetMapping("/test")
    public String save() {
        return "test-" + testConfig + "-" + System.currentTimeMillis();
    }

}
