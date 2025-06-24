package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.common.dto.DriverCarBindingRelationship;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.DriverUserWorkStatus;
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

    public ResponseResult changeWorkStatus(DriverUserWorkStatus driverUserWorkStatus) {
        return serviceDriverUserClient.changeWorkStatus(driverUserWorkStatus);
    }

    public ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(String driverPhone) {
        // 根据driverPhone查询司机信息
        return serviceDriverUserClient.getDriverCarRelationShip(driverPhone);

    }

    public ResponseResult<DriverUserWorkStatus> getWorkStatus(Long driverId) {
        return serviceDriverUserClient.getWorkStatus(driverId);
    }

}
