package com.txai.serviceverificationcode.controller;

import com.txai.common.dto.ResponseResult;
import com.txai.common.response.NumberCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberCodeController {

    @GetMapping("/numberCode/{size}")
    public ResponseResult<NumberCodeResponse> generateNumberCode(@PathVariable("size") int size) {
        System.out.println("size="+size);

        int numberCode = (int) ((Math.random() * 9 + 1) * Math.pow(10, size - 1));
//        JSONObject result = new JSONObject();
//        result.put("numberCode", numberCode);

        return ResponseResult.success(new NumberCodeResponse(numberCode));
    }

}
