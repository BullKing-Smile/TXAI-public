package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServicePassengerUserClient;
import com.txai.apipassenger.remote.ServiceVerficationCodeClient;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.constant.TokenTypeEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import com.txai.common.response.TokenResponse;
import com.txai.common.util.JwtUtils;
import com.txai.common.util.RedisPrefixUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

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

    public ResponseResult<NumberCodeResponse> generateCode(String passengerPhone, int size) {

        System.out.println("调用验证码服务，获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerficationCodeClient.numberCode(size);
        Integer code = numberCodeResponse
                .getData().getNumberCode();
        System.out.println("remote number code: " + code);
        // store code into Redis
        String key = RedisPrefixUtils.getVerificationCodeKey(passengerPhone);
        redisTemplate.opsForValue().set(key, String.valueOf(code), 2, TimeUnit.MINUTES);
        return numberCodeResponse;
    }

    public ResponseResult verificationCodeCheck(VerificationCodeCheckDTO verificationCodeCheckDTO) {
        // query verification code from Redis
        String codeRedis = redisTemplate.opsForValue().get(RedisPrefixUtils.getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));
        if (ObjectUtils.nullSafeEquals(verificationCodeCheckDTO.getVerificationCode(), codeRedis)) {
            System.out.println("Verification code check success");
            // code verify OK
            // then check the user exists or not
            // if not exists do register process
            ResponseResult responseResult = servicePassengerUserClient.loginOrRegister(verificationCodeCheckDTO);
            // then generate token
            String accessToken = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Access.getId());
            String refreshToken = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Refresh.getId());

            TokenResponse tokenDTO = new TokenResponse();
            tokenDTO.setAccessToken(accessToken);
            tokenDTO.setRefreshToken(refreshToken);
            // this logic depends on your requirements
            // delete the code from Redis after verify only compare equaled
            redisTemplate.delete(RedisPrefixUtils.getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));


            // save ACCESS TOKEN to Redis, 24h
            redisTemplate.opsForValue().set(RedisPrefixUtils.getTokenKeyByIdentity(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Access.getValue()), accessToken, 24, TimeUnit.HOURS);
            // keep alive for 30 days for REFRESH TOKEN
            redisTemplate.opsForValue().set(RedisPrefixUtils.getTokenKeyByIdentity(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Refresh.getValue()), refreshToken, 30, TimeUnit.DAYS);


            return ResponseResult.success().setMessage("Verification code is valid").setData(tokenDTO);
        } else {
            return ResponseResult.fail().setMessage("Verification code is invalid");
        }
    }


}
