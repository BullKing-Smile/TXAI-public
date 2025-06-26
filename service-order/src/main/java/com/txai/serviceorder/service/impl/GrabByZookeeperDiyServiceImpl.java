package com.txai.serviceorder.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.serviceorder.service.GrabService;
import com.txai.serviceorder.service.OrderInfoService;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service("grabByZookeeperDiyService")
public class GrabByZookeeperDiyServiceImpl implements GrabService {

    @Autowired
    OrderInfoService orderInfoService;

    @Value("${zookeeper.address}")
    private String address;

    @Value("${zookeeper.timeout}")
    private int timeout;

    @Override
    public ResponseResult grab(DriverGrabRequest driverGrabRequest) {
        ResponseResult grab = null;
        try {
            // 获取锁
            // 连接Zookeeper客户端
            CountDownLatch countDownLatch = new CountDownLatch(1);

            ZooKeeper zooKeeper = new ZooKeeper(address, timeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();

            // 创建 持久节点
            Long orderId = driverGrabRequest.getOrderId();
            String parentNode = "/order-" + orderId;

            Stat exists = zooKeeper.exists(parentNode, false);
            // 没有节点(parentNode) 则创建
            if (exists == null) {
                zooKeeper.create(parentNode, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            // 创建临时节点(子节点)  --- 临时 且 有序
            String seq = "/seq";
            String s = zooKeeper.create(parentNode + seq, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            // 判断我的节点是不是 所有子节点中最小的
            boolean ifLock = false;
            // /order-138/seq0000000000
            if (StringUtils.isNotBlank(s)) {
                // 获取parentNode的所有子节点
                List<String> children = zooKeeper.getChildren(parentNode, false);

                // 再次排序， 保证顺序
                Collections.sort(children);
                // 如果是 刚刚创建的子节点是 列表中的第一个(最小的) 表示拿到了锁
                if ((children.size() > 0) && ((parentNode + "/" + children.get(0)).equals(s))) {
                    ifLock = true;
                    // 执行业务逻辑
                    System.out.println("开始锁 zookeeper diy");
                    grab = orderInfoService.grab(driverGrabRequest);
                    System.out.println("结束锁 zookeeper diy");
                }

            }
            if (!ifLock) {
                grab = ResponseResult.fail(CommonStatusEnum.ORDER_CAN_NOT_GRAB);
            }

            // 释放锁  即使程序宕机挂掉，导致没有机会执行close() zookeeper会自动释放子节点(锁)， 但是程序异常走了catch,是不是在finally代码块中close更合适些?
            // 关闭链接
            zooKeeper.close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return grab;
    }
}
