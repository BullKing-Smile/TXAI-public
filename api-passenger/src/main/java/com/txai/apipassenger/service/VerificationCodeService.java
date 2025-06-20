package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServiceVerficationCodeClient;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import org.json.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private String verificationCodePrefix = "passenger-verification-code-";

    private final ServiceVerficationCodeClient serviceVerficationCodeClient;
    private final StringRedisTemplate redisTemplate;

    public VerificationCodeService(
            ServiceVerficationCodeClient serviceVerficationCodeClient,
            StringRedisTemplate redisTemplate
    ) {
        this.serviceVerficationCodeClient = serviceVerficationCodeClient;
        this.redisTemplate = redisTemplate;
    }

    public String generateCode(String passengerPhone, int size) {

        System.out.println("调用验证码服务，获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerficationCodeClient.numberCode(size);
        Integer code = numberCodeResponse
                .getData().getNumberCode();
        System.out.println("remote number code: " + code);
        // TODO: 2025/6/19 store into Redis
        String key = verificationCodePrefix + passengerPhone;
        redisTemplate.opsForValue().set(key, String.valueOf(code), 2, TimeUnit.MINUTES);

        JSONObject result = new JSONObject();
        result.put("code", code);
        result.put("message", "success");
        return result.toString();
    }

    public ResponseResult verificationCodeCheck(VerificationCodeCheckDTO verificationCodeCheckDTO) {
        String codeRedis = redisTemplate.opsForValue().get(verificationCodePrefix + verificationCodeCheckDTO.getPassengerPhone());
        if (StringUtils.pathEquals(verificationCodeCheckDTO.getVerificationCode(), codeRedis)) {
            System.out.println("Verification code check success");
            // TODO: 2025/6/20 generate token
            return ResponseResult.success().setMessage("Verification code is valid");
        } else {
            return ResponseResult.fail().setMessage("Verification code is invalid");
        }
    }
}
