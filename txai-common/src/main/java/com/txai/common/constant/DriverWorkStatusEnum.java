package com.txai.common.constant;

public enum DriverWorkStatusEnum {
    Stop(0),
    Working(1),
    Suspend(2);

    private final int code;

    DriverWorkStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
