package com.txai.apidriver.interceptor;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.txai.common.constant.TokenTypeEnum;
import com.txai.common.dto.ResponseResult;
import com.txai.common.dto.TokenResult;
import com.txai.common.util.JwtUtils;
import com.txai.common.util.RedisPrefixUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;


public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean result = true;
        String resutltString = "";

        String token = request.getHeader("Authorization");
        // 解析token
        TokenResult tokenResult = JwtUtils.checkToken(token);

        if (tokenResult == null) {
            resutltString = "access token invalid";
            result = false;
        } else {
            // 拼接key
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();

            String tokenKey = RedisPrefixUtils.getTokenKeyByIdentity(phone, identity, TokenTypeEnum.Access.getValue());
            // 从redis中取出token
            String tokenRedis = stringRedisTemplate.opsForValue().get(tokenKey);
            if ((StringUtils.isBlank(tokenRedis)) || (!token.trim().equals(tokenRedis.trim()))) {
                resutltString = "access token invalid";
                result = false;
            }
        }

        if (!result) {
            PrintWriter out = response.getWriter();
            out.print(new JSONObject(ResponseResult.fail().setMessage(resutltString)));
        }

        return result;
    }
}