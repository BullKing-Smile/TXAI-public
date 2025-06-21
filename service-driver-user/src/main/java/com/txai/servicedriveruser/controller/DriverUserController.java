package com.txai.servicedriveruser.controller;

import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import com.txai.servicedriveruser.service.DriverUserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class DriverUserController {

    private final DriverUserService driverUserService;

    public DriverUserController(DriverUserService driverUserService) {
        this.driverUserService = driverUserService;
    }

    @GetMapping("/driver-info/{driverPhone}")
    public ResponseResult<DriverUser> queryDriverInfo(@PathVariable("driverPhone") String driverPhone) {
        return driverUserService.queryDriverInfoByPhone(driverPhone);
    }

    @PostMapping("/driver")
    public ResponseResult save(@RequestBody DriverUser driverUser) {
        return driverUserService.saveDriver(driverUser);
    }

}
