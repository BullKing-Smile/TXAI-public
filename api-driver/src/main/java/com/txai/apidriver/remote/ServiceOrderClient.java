package com.txai.apidriver.remote;

import com.txai.common.dto.OrderInfo;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.DriverGrabRequest;
import com.txai.common.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("service-order")
public interface ServiceOrderClient {

    @RequestMapping(method = RequestMethod.POST, value = "/order/to-pick-up-passenger")
    ResponseResult toPickUpPassenger(@RequestBody OrderRequest orderRequest);

    @RequestMapping(method = RequestMethod.POST, value = "/order/arrived-departure")
    ResponseResult arrivedDeparture(@RequestBody OrderRequest orderRequest);

    @RequestMapping(method = RequestMethod.POST, value = "/order/pick-up-passenger")
    ResponseResult pickUpPassenger(@RequestBody OrderRequest orderRequest);

    @RequestMapping(method = RequestMethod.POST, value = "/order/passenger-getoff")
    ResponseResult passengerGetoff(@RequestBody OrderRequest orderRequest);

    @RequestMapping(method = RequestMethod.POST, value = "/order/cancel")
    ResponseResult cancel(@RequestParam Long orderId, @RequestParam String identity);

    @PostMapping(value = "/order/grab")
    ResponseResult driverGrab(@RequestBody DriverGrabRequest driverGrabRequest);


    @PostMapping("/order/push-pay-info")
    ResponseResult pushPayInfo(@RequestBody OrderRequest orderRequest);

    @RequestMapping(method = RequestMethod.GET, value = "/order/detail")
    ResponseResult<OrderInfo> detail(@RequestParam Long orderId);

    @RequestMapping(method = RequestMethod.GET, value = "/order/current")
    ResponseResult current(@RequestParam String phone, @RequestParam String identity);
}
