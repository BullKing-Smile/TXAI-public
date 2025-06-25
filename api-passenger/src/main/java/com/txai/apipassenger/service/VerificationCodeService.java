package com.txai.apipassenger.service;

import com.txai.apipassenger.remote.ServicePassengerUserClient;
import com.txai.apipassenger.remote.ServiceVerficationCodeClient;
import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.IdentityEnum;
import com.txai.common.constant.TokenTypeEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import com.txai.common.response.TokenResponse;
import com.txai.common.util.JwtUtils;
import com.txai.common.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
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
            try {
                servicePassengerUserClient.loginOrRegister(verificationCodeCheckDTO);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return ResponseResult.fail(CommonStatusEnum.CALL_USER_ADD_ERROR);
            }
            // then generate token
            String accessToken = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Access.getId());
            String refreshToken = JwtUtils.generateToken(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Refresh.getId());

            //  开启Redis 事务支持
            redisTemplate.setEnableTransactionSupport(true);
            SessionCallback<Boolean> callback = new SessionCallback<Boolean>() {
                @Override
                public Boolean execute(RedisOperations operations) throws DataAccessException {
                    // 事务开始
                    redisTemplate.multi();
                    try {
                        // save ACCESS TOKEN to Redis, 24h
                        operations.opsForValue().set(RedisPrefixUtils.getTokenKeyByIdentity(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Access.getValue()), accessToken, 24, TimeUnit.HOURS);
                        // keep alive for 30 days for REFRESH TOKEN
                        operations.opsForValue().set(RedisPrefixUtils.getTokenKeyByIdentity(verificationCodeCheckDTO.getPassengerPhone(), IdentityEnum.Passenger.getId(), TokenTypeEnum.Refresh.getValue()), refreshToken, 30, TimeUnit.DAYS);
                        // 提交
                        operations.exec();
                        return true;
                    } catch (Exception e) {
                        operations.discard();
                        return false;
                    }
                }
            };

            Boolean executeResult = redisTemplate.execute(callback);
            log.info("VerificationCode事务提交|回滚=" + executeResult);
            if (executeResult) {
                TokenResponse tokenDTO = new TokenResponse();
                tokenDTO.setAccessToken(accessToken);
                tokenDTO.setRefreshToken(refreshToken);
                // this logic depends on your requirements
                // delete the code from Redis after verify only compare equaled
                redisTemplate.delete(RedisPrefixUtils.getVerificationCodeKey(verificationCodeCheckDTO.getPassengerPhone()));
                return ResponseResult.success(tokenDTO).setMessage("Verification code is valid");
            } else {
                return ResponseResult.fail(CommonStatusEnum.CHECK_CODE_ERROR);
            }
        }
        return ResponseResult.fail().setMessage("Verification code is invalid");
    }


}
