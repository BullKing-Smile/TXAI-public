package com.txai.common.constant;

public enum CommonStatusEnum {

    SUCCESS(1, "success"),
    FAIL(0, "fail"),
    ACCESS_TOKEN_ERROR(1098, "access token invalid"),
    REFRESH_TOKEN_ERROR(1099, "refresh token invalid"),
    USER_NOT_FOUND(2021, "user not found");

    private int code;
    private String value;

    CommonStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
