package com.txai.apipassenger.controller;

import com.txai.apipassenger.service.UserService;
import com.txai.common.dto.PassengerUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-info")
    public ResponseResult<PassengerUser> userInfo(@RequestHeader("Authorization") String accessToken) {
        return userService.getUserInfoByAccessToken(accessToken);
    }

}
