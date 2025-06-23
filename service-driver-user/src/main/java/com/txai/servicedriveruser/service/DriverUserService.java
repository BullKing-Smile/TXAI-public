package com.txai.servicedriveruser.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.DriverCarConstants;
import com.txai.common.constant.DriverStateEnum;
import com.txai.common.constant.DriverWorkStatusEnum;
import com.txai.common.dto.*;
import com.txai.common.response.OrderDriverResponse;
import com.txai.servicedriveruser.mapper.CarMapper;
import com.txai.servicedriveruser.mapper.DriverCarBindingRelationshipMapper;
import com.txai.servicedriveruser.mapper.DriverUserMapper;
import com.txai.servicedriveruser.mapper.DriverUserWorkStatusMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverUserService {

    private final DriverUserMapper driverUserMapper;
    private final DriverUserWorkStatusMapper driverUserWorkStatusMapper;
    private final DriverCarBindingRelationshipMapper driverCarBindingRelationshipMapper;
    private final CarMapper carMapper;

    public DriverUserService(DriverUserMapper driverUserMapper,
                             DriverUserWorkStatusMapper driverUserWorkStatusMapper,
                             DriverCarBindingRelationshipMapper driverCarBindingRelationshipMapper,
                             CarMapper carMapper) {
        this.driverUserMapper = driverUserMapper;
        this.driverUserWorkStatusMapper = driverUserWorkStatusMapper;
        this.driverCarBindingRelationshipMapper = driverCarBindingRelationshipMapper;
        this.carMapper = carMapper;
    }


    public ResponseResult<DriverUser> queryDriverInfoByPhone(String driverPhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("driver_phone", driverPhone);
        map.put("state", DriverStateEnum.Normal.getCode());
        List<DriverUser> driverUsers = driverUserMapper.selectByMap(map);
        if (null != driverUsers && driverUsers.size() > 0) {
            return ResponseResult.success(driverUsers.get(0));
        }
        return ResponseResult.fail(CommonStatusEnum.DRIVER_USER_NOT_EXISTS);
    }

    public ResponseResult saveDriver(DriverUser driverUser) {
        // set create date
        driverUser.setGmtCreate(LocalDateTime.now());
        int insert = driverUserMapper.insert(driverUser);

        if (1 == insert) {
            // after save success
            // init working status
            DriverUserWorkStatus workStatus = new DriverUserWorkStatus();
            workStatus.setDriverId(driverUser.getId());
            // default stopped
            workStatus.setWorkStatus(DriverWorkStatusEnum.Stop.getCode());
            workStatus.setGmtCreate(LocalDateTime.now());
            driverUserWorkStatusMapper.insert(workStatus);

            return ResponseResult.success().setMessage("Save driver success");
        }
        return ResponseResult.fail().setMessage("Save failed!");
    }

    public ResponseResult updateDriverUser(DriverUser driverUser){
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtModified(now);
        driverUserMapper.updateById(driverUser);
        return ResponseResult.success("");
    }

    public ResponseResult<OrderDriverResponse> getAvailableDriver(Long carId) {
        // 车辆和司机绑定关系查询
        QueryWrapper<DriverCarBindingRelationship> driverCarBindingRelationshipQueryWrapper = new QueryWrapper<>();
        driverCarBindingRelationshipQueryWrapper.eq("car_id", carId);
        driverCarBindingRelationshipQueryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        DriverCarBindingRelationship driverCarBindingRelationship = driverCarBindingRelationshipMapper.selectOne(driverCarBindingRelationshipQueryWrapper);

        if (null == driverCarBindingRelationship) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS);
        }
        Long driverId = driverCarBindingRelationship.getDriverId();
        // 司机工作状态的查询
        QueryWrapper<DriverUserWorkStatus> driverUserWorkStatusQueryWrapper = new QueryWrapper<>();
        driverUserWorkStatusQueryWrapper.eq("driver_id", driverId);
        driverUserWorkStatusQueryWrapper.eq("work_status", DriverCarConstants.DRIVER_WORK_STATUS_START);

        DriverUserWorkStatus driverUserWorkStatus = driverUserWorkStatusMapper.selectOne(driverUserWorkStatusQueryWrapper);
        if (null == driverUserWorkStatus) {
            return ResponseResult.fail(CommonStatusEnum.AVAILABLE_DRIVER_EMPTY);

        } else {
            // 查询司机信息
            QueryWrapper<DriverUser> driverUserQueryWrapper = new QueryWrapper<>();
            driverUserQueryWrapper.eq("id", driverId);
            DriverUser driverUser = driverUserMapper.selectOne(driverUserQueryWrapper);
            // 查询车辆信息
            QueryWrapper<Car> carQueryWrapper = new QueryWrapper<>();
            carQueryWrapper.eq("id", carId);
            Car car = carMapper.selectOne(carQueryWrapper);


            OrderDriverResponse orderDriverResponse = new OrderDriverResponse();
            orderDriverResponse.setCarId(carId);
            orderDriverResponse.setDriverId(driverId);
            orderDriverResponse.setDriverPhone(driverUser.getDriverPhone());

            orderDriverResponse.setLicenseId(driverUser.getLicenseId());
            orderDriverResponse.setVehicleNo(car.getVehicleNo());
            orderDriverResponse.setVehicleType(car.getVehicleType());

            return ResponseResult.success(orderDriverResponse);
        }
    }

}
