package com.txai.serviceorder.service.impl;

import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.serviceorder.service.GrabService;
import com.txai.serviceorder.service.OrderInfoService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("grabByRedissonBasicService")
public class GrabByRedissonBasicServiceImpl implements GrabService {

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    @Qualifier("redissonBootYml")
    RedissonClient redissonClient;

    @Override
    public ResponseResult grab(DriverGrabRequest driverGrabRequest) {

        String orderId = driverGrabRequest.getOrderId() + "";

        String key = orderId;

        RLock lock = redissonClient.getLock(key);
        lock.lock();


        System.out.println("开始锁redis redisson basic");
//        try {
//            TimeUnit.SECONDS.sleep(40);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ResponseResult grab = orderInfoService.grab(driverGrabRequest);
        System.out.println("结束锁redis redisson basic");
        lock.unlock();

        return grab;
    }
}
