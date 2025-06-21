package com.txai.apipassenger.service;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.constant.TokenTypeEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.dto.TokenResult;
import com.txai.common.util.JwtUtils;
import com.txai.common.util.RedisPrefixUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    private StringRedisTemplate redisTemplate;
    public TokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ResponseResult getRefreshToken(String refreshToken) {

        System.out.println("refresh token : "+ refreshToken);
        // parse refresh token
        TokenResult tokenResult = JwtUtils.checkToken(refreshToken);
        if (null == tokenResult) {
            return ResponseResult.fail(CommonStatusEnum.REFRESH_TOKEN_ERROR);
        }

        // retrieve phone,identity
        String phone = tokenResult.getPhone();
        String identity = tokenResult.getIdentity();

        System.out.println("phone="+phone+";identity="+identity);
        // query refresh token from Redis by phone and identity
        String refreshTokenKey = RedisPrefixUtils.getTokenKeyByIdentity(phone, identity, TokenTypeEnum.Refresh.getValue());
        String refreshTokenRedis = redisTemplate.opsForValue().get(refreshTokenKey);

        // not exists in Redis or not equals between client and redis
        if (ObjectUtils.isEmpty(refreshTokenRedis) || !refreshToken.trim().equals(refreshTokenRedis.trim())) {
            System.out.println("The refresh token not exists in redis");
            return ResponseResult.fail(CommonStatusEnum.REFRESH_TOKEN_ERROR);
        }

        // generate new pair token
        String accessTokenNew = JwtUtils.generateToken(phone, identity, TokenTypeEnum.Access.getId());
        String refreshTokenNew = JwtUtils.generateToken(phone, identity, TokenTypeEnum.Refresh.getId());

        // store new tokens to Redis
        redisTemplate.opsForValue().set(refreshTokenKey, refreshTokenNew, 24, TimeUnit.HOURS);
        String accessTokenKey = RedisPrefixUtils.getTokenKeyByIdentity(phone, identity, TokenTypeEnum.Access.getValue());
        redisTemplate.opsForValue().set(accessTokenKey, accessTokenKey, 30, TimeUnit.DAYS);

        // combine response body
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setRefreshToken(refreshTokenNew);
        tokenResponse.setAccessToken(accessTokenNew);
        return ResponseResult.success(tokenResponse);
    }
}
