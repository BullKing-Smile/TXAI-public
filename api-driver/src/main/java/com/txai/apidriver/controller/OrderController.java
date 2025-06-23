package com.txai.apidriver.controller;

import com.txai.apidriver.service.ApiDriverOrderInfoService;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.dto.OrderInfo;
import com.txai.common.dto.ResponseResult;
import com.txai.common.dto.TokenResult;
import com.txai.common.request.OrderRequest;
import com.txai.common.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final ApiDriverOrderInfoService apiDriverOrderInfoService;

    public OrderController(ApiDriverOrderInfoService apiDriverOrderInfoService) {
        this.apiDriverOrderInfoService = apiDriverOrderInfoService;
    }

    /**
     * 去接乘客
     *
     * @param orderRequest
     * @return
     */
    @PostMapping("/to-pick-up-passenger")
    public ResponseResult changeStatus(@RequestBody OrderRequest orderRequest) {

        return apiDriverOrderInfoService.toPickUpPassenger(orderRequest);
    }

    /**
     * 到达乘客上车点
     *
     * @param orderRequest
     * @return
     */
    @PostMapping("/arrived-departure")
    public ResponseResult arrivedDeparture(@RequestBody OrderRequest orderRequest) {
        return apiDriverOrderInfoService.arrivedDeparture(orderRequest);
    }

    /**
     * 司机接到乘客
     *
     * @param orderRequest
     * @return
     */
    @PostMapping("/pick-up-passenger")
    public ResponseResult pickUpPassenger(@RequestBody OrderRequest orderRequest) {
        return apiDriverOrderInfoService.pickUpPassenger(orderRequest);
    }

    /**
     * 乘客到达目的地，行程终止
     *
     * @param orderRequest
     * @return
     */
    @PostMapping("/passenger-getoff")
    public ResponseResult passengerGetoff(@RequestBody OrderRequest orderRequest) {
        return apiDriverOrderInfoService.passengerGetoff(orderRequest);
    }

    @PostMapping("/cancel")
    public ResponseResult cancel(@RequestParam Long orderId) {
        return apiDriverOrderInfoService.cancel(orderId);
    }

    @GetMapping("/detail")
    public ResponseResult<OrderInfo> detail(Long orderId) {
        return apiDriverOrderInfoService.detail(orderId);
    }

    @GetMapping("/current")
    public ResponseResult<OrderInfo> currentOrder(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.parseToken(authorization);
        String identity = tokenResult.getIdentity();
        if (!identity.equals(IdentityEnum.Driver.getId())) {
            return ResponseResult.fail(CommonStatusEnum.ACCESS_TOKEN_ERROR);
        }
        String phone = tokenResult.getPhone();

        return apiDriverOrderInfoService.currentOrder(phone, IdentityEnum.Driver.getId());
    }
}
