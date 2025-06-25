package com.txai.apipassenger.controller;

import com.txai.apipassenger.service.OrderService;
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

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单/下单
     *
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest) {
        System.out.println(orderRequest);
        return orderService.add(orderRequest);
    }

    @PostMapping("/book")
    public ResponseResult book(@RequestBody OrderRequest orderRequest) {
        System.out.println(orderRequest);
        return orderService.book(orderRequest);
    }

    /**
     * 乘客取消订单
     *
     * @param orderId
     * @return
     */
    @PostMapping("/cancel")
    public ResponseResult cancel(@RequestParam Long orderId) {
        return orderService.cancel(orderId);
    }

    @GetMapping("/detail")
    public ResponseResult<OrderInfo> detail(Long orderId) {
        return orderService.detail(orderId);
    }

    @GetMapping("/current")
    public ResponseResult<OrderInfo> currentOrder(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.parseToken(authorization);
        String identity = tokenResult.getIdentity();
        if (!identity.equals(IdentityEnum.Passenger.getId())) {
            return ResponseResult.fail(CommonStatusEnum.ACCESS_TOKEN_ERROR);
        }
        String phone = tokenResult.getPhone();

        return orderService.currentOrder(phone, IdentityEnum.Passenger.getId());
    }

    @GetMapping("/test-real-time-order/{orderId}")
    public String dispatchOrder(@PathVariable("orderId") long orderId) {
        return orderService.dispatchRealTimeOrder(orderId);
    }
}
