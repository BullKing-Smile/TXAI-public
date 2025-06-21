package com.txai.servicedriveruser.service;

import com.txai.common.dto.ResponseResult;
import com.txai.servicedriveruser.mapper.DriverUserMapper;
import org.springframework.stereotype.Service;

@Service
public class CityDriverUserService {

    private final DriverUserMapper driverUserMapper;

    public CityDriverUserService(DriverUserMapper driverUserMapper) {
        this.driverUserMapper = driverUserMapper;
    }

    public ResponseResult<Boolean> isAvailableDriver(String cityCode) {
        int i = driverUserMapper.selectDriverUserCountByCityCode(cityCode);
        if (i > 0) {
            return ResponseResult.success(true);
        } else {
            return ResponseResult.success(false);
        }
    }
}
