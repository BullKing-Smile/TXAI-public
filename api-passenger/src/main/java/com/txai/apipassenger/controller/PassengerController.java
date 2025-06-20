package com.txai.apipassenger.controller;

import com.txai.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassengerController {

    @GetMapping("/test")
    public String test() {
        return "{\"data\":\"test" + System.currentTimeMillis() + "\"}";
    }

    @GetMapping("/auth")
    public ResponseResult auth() {
        return ResponseResult.success();
    }

    @GetMapping("/noauth")
    public ResponseResult noauth() {
        return ResponseResult.success();
    }
}
