package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceOrderClient;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.dto.OrderInfo;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.OrderRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class ApiDriverOrderInfoService {

    private final ServiceOrderClient serviceOrderClient;

    public ApiDriverOrderInfoService(ServiceOrderClient serviceOrderClient) {
        this.serviceOrderClient = serviceOrderClient;
    }

    public ResponseResult toPickUpPassenger(OrderRequest orderRequest) {
        return serviceOrderClient.toPickUpPassenger(orderRequest);
    }

    public ResponseResult arrivedDeparture(OrderRequest orderRequest) {
        return serviceOrderClient.arrivedDeparture(orderRequest);
    }

    /**
     * 司机接到乘客
     *
     * @param orderRequest
     * @return
     */
    public ResponseResult pickUpPassenger(@RequestBody OrderRequest orderRequest) {
        return serviceOrderClient.pickUpPassenger(orderRequest);
    }

    public ResponseResult passengerGetoff(OrderRequest orderRequest) {
        return serviceOrderClient.passengerGetoff(orderRequest);
    }

    public ResponseResult cancel(Long orderId) {
        return serviceOrderClient.cancel(orderId, IdentityEnum.Driver.getId());
    }

    public ResponseResult<OrderInfo> detail(Long orderId) {
        return serviceOrderClient.detail(orderId);
    }


    public ResponseResult<OrderInfo> currentOrder(String phone, String identity) {
        return serviceOrderClient.current(phone, identity);
    }
}
