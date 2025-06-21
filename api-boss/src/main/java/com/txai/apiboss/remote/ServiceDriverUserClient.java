package com.txai.apiboss.remote;

import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "service-driver-user")
public interface ServiceDriverUserClient {

    @PostMapping("/driver")
    ResponseResult saveDriver(@RequestBody DriverUser driverUser);

}
