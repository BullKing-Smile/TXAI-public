package com.txai.servicedriveruser.controller;

import com.txai.common.dto.Car;
import com.txai.common.dto.ResponseResult;
import com.txai.servicedriveruser.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CarController {

    @Autowired
    CarService carService;

    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

    @GetMapping("/car/{carId}")
    public ResponseResult<Car> getCarById(@PathVariable("carId") Long carId) {
        return carService.getCarById(carId);
    }
}
