package com.txai.servicepassengeruser.service;

import com.txai.common.dto.ResponseResult;
import com.txai.servicepassengeruser.dto.PassengerUser;
import com.txai.servicepassengeruser.mapper.PassengerUserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final PassengerUserMapper passengerUserMapper;
    public UserService(PassengerUserMapper passengerUserMapper) {
        this.passengerUserMapper = passengerUserMapper;
    }

    public ResponseResult loginOrRegister(String passengerPhone, String code) {

        // query user's info by passenger phone
        Map<String, Object> map = new HashMap<>();
        map.put("passenger_phone", passengerPhone);
        List<PassengerUser> passengerUsers = passengerUserMapper.selectByMap(map);

        System.out.println(passengerUsers == null || passengerUsers.size() == 0 ? "没找到user": "success");
        // it's exists or not
        // if not exists do register process
        if (null == passengerUsers || passengerUsers.size() == 0) {
            PassengerUser user = new PassengerUser();
            user.setPassengerName(passengerPhone);
            user.setPassengerPhone(passengerPhone);
            user.setGmtCreate(LocalDateTime.now());
            int result = passengerUserMapper.insert(user);
            System.out.println(1 == result ? "Save passenger user success!": "save user failed");
            return ResponseResult.success().setMessage("Register success");
        } else {


            return ResponseResult.success().setMessage("Login success");

        }
    }
}
