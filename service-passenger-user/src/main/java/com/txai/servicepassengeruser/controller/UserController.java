package com.txai.servicepassengeruser.controller;

import com.txai.common.dto.PassengerUser;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.servicepassengeruser.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/loginOrRegister")
    public ResponseResult logOrReg(@RequestBody VerificationCodeCheckDTO verificationCodeCheckDTO) {

        String passengerPhone = verificationCodeCheckDTO.getPassengerPhone();
        String code = verificationCodeCheckDTO.getVerificationCode();
        ;

        System.out.println("phone:" + passengerPhone + ";code:" + code);

        return userService.loginOrRegister(passengerPhone, code);
    }

    @GetMapping("/user/{passengerPhone}")
    public ResponseResult<PassengerUser> logOrReg(@PathVariable("passengerPhone") String passengerPhone) {
        return userService.getUserInfoByAccessToken(passengerPhone);
    }

}
