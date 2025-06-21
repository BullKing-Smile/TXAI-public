package com.txai.common.constant;

public enum DriverStateEnum {
    Normal(1), Invalid(0);

    private final int code;

    DriverStateEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
