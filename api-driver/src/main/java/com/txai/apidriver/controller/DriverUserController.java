package com.txai.apidriver.controller;

import com.txai.apidriver.service.CarService;
import com.txai.apidriver.service.DriverUserService;
import com.txai.common.dto.*;
import com.txai.common.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 添加车辆
     *
     * @param car new car data
     * @return
     */
    @PostMapping("/car")
    public ResponseResult car(@RequestBody Car car) {
        return carService.addCar(car);
    }


    /**
     * 更新司机工作状态
     *
     * @param driverUserWorkStatus 司机工作状态
     * @return
     */
    @PostMapping("/driver-user-work-status")
    public ResponseResult changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus){

        return driverUserService.changeWorkStatus(driverUserWorkStatus);
    }

    /**
     * 根据司机token查询 司机和车辆绑定关系
     * @param request
     * @return
     */
    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult getDriverCarBindingRelationship(HttpServletRequest request){

        String authorization = request.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.checkToken(authorization);
        String driverPhone = tokenResult.getPhone();

        return driverUserService.getDriverCarBindingRelationship(driverPhone);

    }

    /**
     * 查看司机工作状态
     * @param driverId 司机id
     * @return
     */
    @GetMapping("/work-status")
    public ResponseResult<DriverUserWorkStatus> getWorkStatus(Long driverId){
        return driverUserService.getWorkStatus(driverId);
    }
}
