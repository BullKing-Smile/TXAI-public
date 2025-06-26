package com.txai.serviceorder.service.impl;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.serviceorder.service.GrabService;
import com.txai.serviceorder.service.OrderInfoService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service("grabByZookeeperCuratorService")
public class GrabByZookeeperCuratorServiceImpl implements GrabService {

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    CuratorFramework curatorFramework;

    @Override
    public ResponseResult grab(DriverGrabRequest driverGrabRequest) {
        ResponseResult grab = null;
        // 创建 持久节点
        Long orderId = driverGrabRequest.getOrderId();
        String parentNode = "/order-"+orderId;

        InterProcessMutex lock = new InterProcessMutex(curatorFramework,parentNode);

        try {
            if (lock.acquire(10, TimeUnit.SECONDS)){
                System.out.println("开始锁 zookeeper curator");
                grab = orderInfoService.grab(driverGrabRequest);
                System.out.println("结束锁 zookeeper curator");
            }else {
                grab = ResponseResult.fail(CommonStatusEnum.ORDER_CAN_NOT_GRAB);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return grab;
    }
}
