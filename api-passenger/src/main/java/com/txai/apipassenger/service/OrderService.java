package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServiceOrderClient;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.dto.OrderInfo;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.OrderRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final ServiceOrderClient serviceOrderClient;

    public OrderService(ServiceOrderClient serviceOrderClient) {
        this.serviceOrderClient = serviceOrderClient;
    }

    public ResponseResult add(OrderRequest orderRequest) {
        return serviceOrderClient.add(orderRequest);
    }

    public ResponseResult book(OrderRequest orderRequest) {
        return serviceOrderClient.book(orderRequest);
    }

    /**
     * 取消订单
     *
     * @param orderId
     * @returno
     */
    public ResponseResult cancel(Long orderId) {
        return serviceOrderClient.cancel(orderId, IdentityEnum.Passenger.getId());
    }


    public ResponseResult<OrderInfo> detail(Long orderId) {
        return serviceOrderClient.detail(orderId);
    }

    public ResponseResult<OrderInfo> currentOrder(String phone, String identity) {
        return serviceOrderClient.current(phone, identity);
    }

    public String dispatchRealTimeOrder(Long orderId) {
        return serviceOrderClient.dispatchRealTimeOrder(orderId);
    }


}
