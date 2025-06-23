package com.txai.servicedriveruser.controller;

import com.txai.common.constant.DriverCarConstants;
import com.txai.common.dto.DriverCarBindingRelationship;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.DriverUserExistsResponse;
import com.txai.common.response.OrderDriverResponse;
import com.txai.servicedriveruser.service.DriverCarBindingRelationshipService;
import com.txai.servicedriveruser.service.DriverUserService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    private final DriverUserService driverUserService;

    private final DriverCarBindingRelationshipService driverCarBindingRelationshipService;

    public UserController(DriverUserService driverUserService,
                          DriverCarBindingRelationshipService driverCarBindingRelationshipService) {
        this.driverUserService = driverUserService;
        this.driverCarBindingRelationshipService = driverCarBindingRelationshipService;
    }

    /**
     * 新增司机
     *
     * @param driverUser
     * @return
     */
    @PostMapping("/user")
    public ResponseResult addUser(@RequestBody DriverUser driverUser) {
        log.info(new JSONObject(driverUser).toString());
        return driverUserService.saveDriver(driverUser);
    }

    /**
     * 修改司机
     *
     * @param driverUser
     * @return
     */
    @PutMapping("/user")
    public ResponseResult updateUser(@RequestBody DriverUser driverUser) {
        log.info(new JSONObject(driverUser).toString());
        return driverUserService.updateDriverUser(driverUser);
    }

    /**
     * 查询 司机
     * 如果需要按照司机的多个条件做查询，那么此处用 /user
     *
     * @return
     */
    @GetMapping("/check-driver/{driverPhone}")
    public ResponseResult<DriverUserExistsResponse> getUser(@PathVariable("driverPhone") String driverPhone) {

        ResponseResult<DriverUser> driverUserByPhone = driverUserService.queryDriverInfoByPhone(driverPhone);
        DriverUser driverUserDb = driverUserByPhone.getData();

        DriverUserExistsResponse response = new DriverUserExistsResponse();

        int ifExists = DriverCarConstants.DRIVER_EXISTS;
        if (driverUserDb == null) {
            ifExists = DriverCarConstants.DRIVER_NOT_EXISTS;
            response.setDriverPhone(driverPhone);
            response.setIfExists(ifExists);
        } else {
            response.setDriverPhone(driverUserDb.getDriverPhone());
            response.setIfExists(ifExists);
        }

        return ResponseResult.success(response);
    }

    /**
     * 根据车辆Id查询订单需要的司机信息
     *
     * @param carId
     * @return
     */
    @GetMapping("/get-available-driver/{carId}")
    public ResponseResult<OrderDriverResponse> getAvailableDriver(@PathVariable("carId") Long carId) {
        return driverUserService.getAvailableDriver(carId);
    }

    /**
     * 根据司机手机号查询司机和车辆绑定关系
     *
     * @param driverPhone
     * @return
     */
    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult<DriverCarBindingRelationship> getDriverCarRelationShip(@RequestParam String driverPhone) {
        return driverCarBindingRelationshipService.getDriverCarRelationShipByDriverPhone(driverPhone);
    }

}
