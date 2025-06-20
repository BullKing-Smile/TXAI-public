package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServicePassengerUserClient;
import com.txai.apipassenger.remote.ServiceVerficationCodeClient;
import com.txai.apipassenger.response.TokenDTO;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import com.txai.common.util.JwtUtils;
import org.json.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private String verificationCodePrefix = "passenger-verification-code-";

    private final ServiceVerficationCodeClient serviceVerficationCodeClient;
    private final ServicePassengerUserClient servicePassengerUserClient;
    private final StringRedisTemplate redisTemplate;

    public VerificationCodeService(
            ServiceVerficationCodeClient serviceVerficationCodeClient,
            ServicePassengerUserClient servicePassengerUserClient,
            StringRedisTemplate redisTemplate
    ) {
        this.serviceVerficationCodeClient = serviceVerficationCodeClient;
        this.servicePassengerUserClient = servicePassengerUserClient;
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
        // query verification code from Redis
        String codeRedis = redisTemplate.opsForValue().get(getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));
        if (ObjectUtils.nullSafeEquals(verificationCodeCheckDTO.getVerificationCode(), codeRedis)) {
            System.out.println("Verification code check success");
            // code verify OK
            // then check the user exists or not
            // if not exists do register process
            ResponseResult responseResult = servicePassengerUserClient.loginOrRegister(verificationCodeCheckDTO);
            // then generate token
            String token = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId());
            TokenDTO tokenDTO = new TokenDTO();
            tokenDTO.setToken(token);
            // this logic depends on your requirements
            // delete the code from Redis after verify only compare equaled
            redisTemplate.delete(getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));

            return ResponseResult.success().setMessage("Verification code is valid").setData(tokenDTO);
        } else {
            return ResponseResult.fail().setMessage("Verification code is invalid");
        }
    }

    private String getVerificationCodeKey(String phone) {
        return verificationCodePrefix + phone;
    }
}
