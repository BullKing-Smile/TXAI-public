package com.txai.pay.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.txai.pay.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/alipay")
@Controller
@ResponseBody
public class AlipayController {

    private AlipayService alipayService;

    public AlipayController(AlipayService alipayService) {
        this.alipayService = alipayService;
    }

    @PostMapping("/test")
    public String test() {
        log.info("test-success-post-" + System.currentTimeMillis());
        return "test-success-post-" + System.currentTimeMillis();
    }

    @PostMapping("/test2")
    public String test2() {
        log.info("test2-success-post-" + System.currentTimeMillis());
        return "test2-success-post-" + System.currentTimeMillis();
    }


    @GetMapping("/pay")
    public String pay(String subject, String outTradeNo, String totalAmount) {
        AlipayTradePagePayResponse response;
        try {
            response = Factory.Payment.Page().pay(subject, outTradeNo, totalAmount, "");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return response.getBody();
    }

    @PostMapping("/notify")
    public String notify(HttpServletRequest request) throws Exception {
        log.info("支付宝回调 notify");
        String tradeStatus = request.getParameter("trade_status");
        if (tradeStatus.trim().equals("TRADE_SUCCESS")) {
            Map<String, String> param = new HashMap<>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String name : parameterMap.keySet()) {
                param.put(name, request.getParameter(name));
            }
            // out_trade_no 订单号
            // total_amount 支付价格
            // subject 商品名称
            if (Factory.Payment.Common().verifyNotify(param)) {
                log.info("通过支付宝的验证");
                String out_trade_no = param.get("out_trade_no");
                Long orderId = Long.parseLong(out_trade_no);
                alipayService.pay(orderId);
            } else {
                log.info("支付宝验证 不通过！");
            }
        }
        return "success";
    }
}