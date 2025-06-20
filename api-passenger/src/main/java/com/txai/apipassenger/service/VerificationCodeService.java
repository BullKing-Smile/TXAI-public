package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServiceVerficationCodeClient;
import com.txai.common.dto.ResponseResult;
import com.txai.common.response.NumberCodeResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {


    private final ServiceVerficationCodeClient serviceVerficationCodeClient;

    public VerificationCodeService(
            ServiceVerficationCodeClient serviceVerficationCodeClient
    ) {
        this.serviceVerficationCodeClient = serviceVerficationCodeClient;
    }

    public String generateCode(String passengerPhone, int size) {

        System.out.println("调用验证码服务，获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerficationCodeClient.numberCode(size);
        Integer code = numberCodeResponse
                .getData().getNumberCode();
        System.out.println("remote number code: " + code);
        // TODO: 2025/6/19 store into Redis

        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("message", "success");
        return result.toString();
    }
}
