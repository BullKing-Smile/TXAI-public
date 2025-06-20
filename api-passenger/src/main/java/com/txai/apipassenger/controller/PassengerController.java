package com.txai.apipassenger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassengerController {

    @GetMapping("/test")
    public String test() {
        return "{\"data\":\"test"+ System.currentTimeMillis()+"\"}";
    }
}
