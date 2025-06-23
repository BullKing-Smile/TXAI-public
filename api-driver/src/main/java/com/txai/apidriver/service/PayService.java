package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceOrderClient;
import com.txai.apidriver.remote.ServiceSsePushClient;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.OrderRequest;
import com.txai.common.request.PushRequest;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    private final ServiceSsePushClient serviceSsePushClient;

    private final ServiceOrderClient serviceOrderClient;

    public PayService(ServiceSsePushClient serviceSsePushClient,
                      ServiceOrderClient serviceOrderClient) {
        this.serviceOrderClient = serviceOrderClient;
        this.serviceSsePushClient = serviceSsePushClient;
    }

    public ResponseResult pushPayInfo(Long orderId, String price, Long passengerId) {
        // 封装消息
        JSONObject message = new JSONObject();
        message.put("price", price);
        message.put("orderId", orderId);
        // 修改订单状态
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(orderId);
        serviceOrderClient.pushPayInfo(orderRequest);

        PushRequest pushRequest = new PushRequest();
        pushRequest.setContent(message.toString());
        pushRequest.setUserId(passengerId);
        pushRequest.setIdentity(IdentityEnum.Passenger.getId());

        // 推送消息
        serviceSsePushClient.push(pushRequest);

        return ResponseResult.success();
    }
}
