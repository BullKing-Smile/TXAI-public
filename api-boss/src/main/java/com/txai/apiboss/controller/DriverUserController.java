package com.txai.apiboss.controller;

import com.txai.apiboss.service.DriverUserService;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverUserController {

    private final DriverUserService driverUserService;

    public DriverUserController(DriverUserService driverUserService) {
        this.driverUserService = driverUserService;
    }


    @PostMapping("/driver")
    public ResponseResult save(@RequestBody DriverUser driverUser) {
        return driverUserService.saveDriver(driverUser);
    }

}
