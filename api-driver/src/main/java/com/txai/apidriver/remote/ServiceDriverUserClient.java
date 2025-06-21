package com.txai.apidriver.remote;

import com.txai.common.dto.Car;
import com.txai.common.dto.DriverCarBindingRelationship;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "service-driver-user")
public interface ServiceDriverUserClient {

    @GetMapping("/driver-info/{driverPhone}")
    ResponseResult<DriverUser> getDriverUser(@PathVariable("driverPhone") String driverPhone);

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    ResponseResult addDriverUser(@RequestBody DriverUser driverUser);

    @RequestMapping(method = RequestMethod.PUT, value = "/user")
    ResponseResult updateDriverUser(@RequestBody DriverUser driverUser);

    @RequestMapping(method = RequestMethod.POST, value = "/car")
    ResponseResult addCar(@RequestBody Car car);

    @RequestMapping(method = RequestMethod.GET, value = "/car/{carId}")
    ResponseResult<Car> getCarById(@PathVariable("carId") Long carId);

    @RequestMapping(method = RequestMethod.POST, value = "/driver-car-binding-relationship/bind")
    ResponseResult bind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship);

    @RequestMapping(method = RequestMethod.POST, value = "/driver-car-binding-relationship/unbind")
    ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship);

}
