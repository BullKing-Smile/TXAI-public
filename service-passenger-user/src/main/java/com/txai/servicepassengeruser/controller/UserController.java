package com.txai.servicepassengeruser.controller;

import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.servicepassengeruser.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/loginOrRegister")
    public ResponseResult logOrReg(@RequestBody VerificationCodeCheckDTO verificationCodeCheckDTO) {

        String passengerPhone = verificationCodeCheckDTO.getPassengerPhone();
        String code = verificationCodeCheckDTO.getVerificationCode();;

        System.out.println("phone:"+passengerPhone+";code:"+code);

        return userService.loginOrRegister(passengerPhone, code);
    }

}
