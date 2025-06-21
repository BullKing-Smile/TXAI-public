package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.common.dto.Car;
import com.txai.common.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    private final ServiceDriverUserClient serviceDriverUserClient;

    public CarService(ServiceDriverUserClient serviceDriverUserClient) {
        this.serviceDriverUserClient = serviceDriverUserClient;
    }

    public ResponseResult addCar(Car car) {
        return serviceDriverUserClient.addCar(car);
    }
}
