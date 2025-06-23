package com.txai.servicessepush.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/index.html")
    public String driverClientPage() {
        return "driver-client.html";
    }
}
