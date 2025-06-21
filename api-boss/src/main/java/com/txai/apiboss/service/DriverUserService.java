package com.txai.apiboss.service;

import com.txai.apiboss.remote.ServiceDriverUserClient;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class DriverUserService {

    private final ServiceDriverUserClient serviceDriverUserClient;

    public DriverUserService(ServiceDriverUserClient serviceDriverUserClient) {
        this.serviceDriverUserClient = serviceDriverUserClient;
    }


    public ResponseResult saveDriver(DriverUser driverUser) {
        return serviceDriverUserClient.saveDriver(driverUser);
    }
}
