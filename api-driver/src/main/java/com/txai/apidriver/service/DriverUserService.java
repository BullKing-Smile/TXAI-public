package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class DriverUserService {

    private final ServiceDriverUserClient serviceDriverUserClient;

    public DriverUserService(ServiceDriverUserClient serviceDriverUserClient) {
        this.serviceDriverUserClient = serviceDriverUserClient;
    }

    public ResponseResult addDriverUser(DriverUser driverUser) {
        return serviceDriverUserClient.addDriverUser(driverUser);
    }

    public ResponseResult updateDriverUser(DriverUser driverUser) {
        return serviceDriverUserClient.updateDriverUser(driverUser);
    }

}
