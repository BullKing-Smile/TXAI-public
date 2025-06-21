package com.txai.apidriver.controller;

import com.txai.apidriver.service.CarService;
import com.txai.apidriver.service.DriverUserService;
import com.txai.common.dto.Car;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverUserController {

    private final DriverUserService driverUserService;
    private final CarService carService;

    public DriverUserController(DriverUserService driverUserService,
                                CarService carService) {
        this.driverUserService = driverUserService;
        this.carService = carService;
    }

    /**
     * 添加司机
     *
     * @param driverUser
     * @return
     */
    @PostMapping("/driver-user")
    public ResponseResult addDriverUser(@RequestBody DriverUser driverUser) {
        return driverUserService.addDriverUser(driverUser);
    }

    /**
     * 修改司机
     *
     * @param driverUser
     * @return
     */
    @PutMapping("/driver-user")
    public ResponseResult updateDriverUser(@RequestBody DriverUser driverUser) {
        return driverUserService.updateDriverUser(driverUser);
    }

    @PostMapping("/car")
    public ResponseResult car(@RequestBody Car car) {
        return carService.addCar(car);
    }


}
