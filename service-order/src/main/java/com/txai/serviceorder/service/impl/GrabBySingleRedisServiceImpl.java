package com.txai.serviceorder.service.impl;

import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.serviceorder.service.GrabService;
import com.txai.serviceorder.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("grabBySingleRedisService")
public class GrabBySingleRedisServiceImpl implements GrabService {

    @Autowired
    OrderInfoService orderInfoService;

    @Override
    public ResponseResult grab(DriverGrabRequest driverGrabRequest) {

        System.out.println("开始锁single redis");
        ResponseResult grab = orderInfoService.grab(driverGrabRequest);
        System.out.println("结束锁single redis");

        return grab;
    }
}
