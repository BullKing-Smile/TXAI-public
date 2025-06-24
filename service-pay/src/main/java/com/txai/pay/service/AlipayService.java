package com.txai.pay.service;

import com.txai.common.request.OrderRequest;
import com.txai.pay.remote.ServiceOrderClient;
import org.springframework.stereotype.Service;

@Service
public class AlipayService {

    private final ServiceOrderClient serviceOrderClient;

    public AlipayService(ServiceOrderClient serviceOrderClient) {
        this.serviceOrderClient = serviceOrderClient;
    }

    public void pay(Long orderId) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(orderId);
        serviceOrderClient.pay(orderRequest);

    }
}
