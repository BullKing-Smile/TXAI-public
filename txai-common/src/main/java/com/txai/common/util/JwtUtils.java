package com.txai.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.txai.common.dto.TokenResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    //salt
    private static final String SECRET = "8alH3mjfosikk-0pY2b9i";
    private static final String JWT_KEY_PHONE = "phone";
    // 1是乘客   2是司机
    private static final String JWT_KEY_IDENTITY = "identity";
    public static String generateToken(String phone, String identity) {
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, phone);
        map.put(JWT_KEY_IDENTITY, identity);
        return generateToken(map);
    }

    public static String generateToken(Map<String, String> map) {

        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();
        // append header
        builder.withHeader(header);
        // append claim
        map.forEach(builder::withClaim);
        //set expiry date
        builder.withExpiresAt(date);
        String token = builder.sign(Algorithm.HMAC256(SECRET));
        return token;
    }

//    public static boolean verifyToken(String token) {
//        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(SECRET)).build()
//                .verify(token);
//        return true;
//    }

    public static TokenResult parseToken(String token) {
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SECRET)).build()
                .verify(token);
        TokenResult tokenResult = new TokenResult();
        tokenResult.setPhone(verify.getClaim(JWT_KEY_PHONE).asString());
        tokenResult.setIdentity(verify.getClaim(JWT_KEY_IDENTITY).asString());
        return tokenResult;
    }
/*
    public static void main(String[] args) {
        for (int i = 10; i > 0; i--) {
            String phone  = String.valueOf((long) (Math.random() * 10000000000000l));
            String token = generateToken(phone);
            System.out.println("随机手机号="+phone);
            System.out.println(token);
            System.out.println("解析的phone="+ parseToken(token));
            System.out.println("verify result is = " + verifyToken(token + (i % 3 == 0 ? "1" : "")));

        }
    }*/
}
