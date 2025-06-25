package com.txai.serviceorder.service.impl;

import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.serviceorder.service.GrabService;
import com.txai.serviceorder.service.OrderInfoService;
import org.springframework.stereotype.Service;

@Service("grabByMultiRedisService")
public class GrabByMultiRedisServiceImpl implements GrabService {

    private final OrderInfoService orderInfoService;

    public GrabByMultiRedisServiceImpl(OrderInfoService orderInfoService) {
        this.orderInfoService = orderInfoService;
    }

    @Override
    public ResponseResult grab(DriverGrabRequest driverGrabRequest) {

        System.out.println("开始锁 multi  Redis");
        ResponseResult grab = orderInfoService.grab(driverGrabRequest);
        System.out.println("结束锁 multi Redis");

        return grab;
    }
}
