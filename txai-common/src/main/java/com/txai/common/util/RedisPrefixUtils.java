package com.txai.common.util;

public class RedisPrefixUtils {

    private final static String verificationCodePrefix = "passenger-verification-code-";
    private final static String tokenPrefix = "token-";

    public static final String blackDeviceCodePrefix = "black-device-code-";

    public static String getTokenKeyByIdentity(String phone, String identity, String tokenType) {
        return tokenPrefix + phone + "-" + identity + "-" + tokenType;
    }

    public static String getVerificationCodeKey(String phone) {
        return verificationCodePrefix + phone;
    }
}
