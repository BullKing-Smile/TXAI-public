package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.common.dto.DriverCarBindingRelationship;
import com.txai.common.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class DriverCarBindingRelationshipService {

    private final ServiceDriverUserClient serviceDriverUserClient;

    public DriverCarBindingRelationshipService(ServiceDriverUserClient serviceDriverUserClient) {
        this.serviceDriverUserClient = serviceDriverUserClient;
    }

    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship) {
        return serviceDriverUserClient.bind(driverCarBindingRelationship);
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship) {

        return serviceDriverUserClient.unbind(driverCarBindingRelationship);
    }
}
