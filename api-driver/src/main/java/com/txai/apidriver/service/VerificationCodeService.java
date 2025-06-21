package com.txai.apidriver.service;

import com.txai.apidriver.remote.ServiceDriverUserClient;
import com.txai.apidriver.remote.ServiceVerficationCodeClient;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.constant.TokenTypeEnum;
import com.txai.common.dto.DriverUser;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import com.txai.common.response.TokenResponse;
import com.txai.common.util.JwtUtils;
import com.txai.common.util.RedisPrefixUtils;
import org.json.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private final ServiceVerficationCodeClient serviceVerficationCodeClient;
    private final ServiceDriverUserClient serviceDriverUserClient;
    private final StringRedisTemplate redisTemplate;

    public VerificationCodeService(
            ServiceVerficationCodeClient serviceVerficationCodeClient,
            ServiceDriverUserClient serviceDriverUserClient,
            StringRedisTemplate redisTemplate
    ) {
        this.serviceVerficationCodeClient = serviceVerficationCodeClient;
        this.serviceDriverUserClient = serviceDriverUserClient;
        this.redisTemplate = redisTemplate;
    }

    public ResponseResult<NumberCodeResponse> generateCode(String driverPhone) {

        // first step , check driver user exists or not
        // if not exists, directly return failed message
        ResponseResult<DriverUser> driverUser = serviceDriverUserClient.getDriverUser(driverPhone);

        if (null == driverUser || null == driverUser.getData()) {
            return ResponseResult.fail(CommonStatusEnum.DRIVER_USER_NOT_EXISTS);
        }

        // if driver user is exists, generate code pls

        System.out.println("调用验证码服务，获取验证码");
        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerficationCodeClient.numberCode(4);
        Integer code = numberCodeResponse
                .getData().getNumberCode();
        System.out.println("remote number code: " + code);
        // store code into Redis
        String key = RedisPrefixUtils.getVerificationCodeKey(driverPhone);
        redisTemplate.opsForValue().set(key, String.valueOf(code), 2, TimeUnit.MINUTES);
        return numberCodeResponse;
    }

    public ResponseResult verificationCodeCheck(VerificationCodeCheckDTO verificationCodeCheckDTO) {
        // query verification code from Redis
        String codeRedis = redisTemplate.opsForValue().get(RedisPrefixUtils.getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));
        if (ObjectUtils.nullSafeEquals(verificationCodeCheckDTO.getVerificationCode(), codeRedis)) {
            System.out.println("Verification code check success");
            // code verify OK


            // then generate token
            String accessToken = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Driver.getId(), TokenTypeEnum.Access.getId());
            String refreshToken = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Driver.getId(), TokenTypeEnum.Refresh.getId());

            TokenResponse tokenDTO = new TokenResponse();
            tokenDTO.setAccessToken(accessToken);
            tokenDTO.setRefreshToken(refreshToken);
            // this logic depends on your requirements
            // delete the code from Redis after verify only compare equaled
            redisTemplate.delete(RedisPrefixUtils.getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));


            // save ACCESS TOKEN to Redis, 24h
            redisTemplate.opsForValue().set(RedisPrefixUtils.getTokenKeyByIdentity(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Driver.getId(), TokenTypeEnum.Access.getValue()), accessToken, 24, TimeUnit.HOURS);
            // keep alive for 30 days for REFRESH TOKEN
            redisTemplate.opsForValue().set(RedisPrefixUtils.getTokenKeyByIdentity(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Driver.getId(), TokenTypeEnum.Refresh.getValue()), refreshToken, 30, TimeUnit.DAYS);


            return ResponseResult.success().setMessage("Verification code is valid").setData(tokenDTO);
        } else {
            return ResponseResult.fail().setMessage("Verification code is invalid");
        }
    }


}
