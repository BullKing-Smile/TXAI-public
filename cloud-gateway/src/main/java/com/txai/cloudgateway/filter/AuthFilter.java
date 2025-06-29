package com.txai.cloudgateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.txai.common.constant.TokenTypeEnum;
import com.txai.common.dto.TokenResult;
import com.txai.common.util.JwtUtils;
import com.txai.common.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter {

    List<String> authPath = Arrays.asList("xxx");

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        System.out.println("请求来了:" + request.getPath());
        String path = request.getPath().toString();
        boolean flag = true;
        String result = "";

        if (!authPath.contains(path)) {
            System.out.println("需要校验auth");
            String token = request.getHeaders().getFirst("Authorization");

            // 解析token
            TokenResult tokenResult = JwtUtils.checkToken(token);

            if (tokenResult == null) {
                result = "access token invalid";
                flag = false;
            } else {
                // 拼接key
                String phone = tokenResult.getPhone();
                String identity = tokenResult.getIdentity();

                String tokenKey = RedisPrefixUtils.getTokenKeyByIdentity(phone, identity, TokenTypeEnum.Access.getValue());
                // 从redis中取出token
                String tokenRedis = stringRedisTemplate.opsForValue().get(tokenKey);
                if ((StringUtils.isBlank(tokenRedis)) || (!token.trim().equals(tokenRedis.trim()))) {
                    result = "access token invalid";
                    flag = false;
                }
            }

        } else {
            System.out.println("不需要校验auth");
        }

        // 判断标识
        if (flag) {
            return chain.filter(exchange);
        } else {
            // 校验不通过
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            return response.setComplete();
        }
    }
}
