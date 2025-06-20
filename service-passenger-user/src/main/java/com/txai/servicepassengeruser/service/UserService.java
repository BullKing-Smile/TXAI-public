package com.txai.servicepassengeruser.service;

import com.txai.common.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public ResponseResult loginOrRegister(String passengerPhone, String code) {

        // query user's info by passenger phone


        // it's exists or not

        // if not exists do register process

        return ResponseResult.success();
    }
}
