package com.txai.servicedriveruser.service;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.DriverStateEnum;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import com.txai.servicedriveruser.mapper.DriverUserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverUserService {

    private final DriverUserMapper driverUserMapper;
    public DriverUserService(DriverUserMapper driverUserMapper) {
        this.driverUserMapper = driverUserMapper;
    }


    public ResponseResult<DriverUser> queryDriverInfoByPhone(String driverPhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("driver_phone", driverPhone);
        map.put("state", DriverStateEnum.Normal.getCode());
        List<DriverUser> driverUsers = driverUserMapper.selectByMap(map);
        if (null != driverUsers && driverUsers.size() > 0) {
            return ResponseResult.success(driverUsers.get(0));
        }
        return ResponseResult.fail(CommonStatusEnum.DRIVER_USER_NOT_EXISTS);
    }

    public ResponseResult saveDriver(DriverUser driverUser) {
        // set create date
        driverUser.setGmtCreate(LocalDateTime.now());
        int insert = driverUserMapper.insert(driverUser);
        if (1 == insert) {
            return ResponseResult.success().setMessage("Save driver success");
        }
        return ResponseResult.fail().setMessage("Save failed!");
    }
}
