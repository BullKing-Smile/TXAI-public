package com.txai.common.constant;

/**
 * 撤销类型代码
 * 1:乘客提前撤销
 * 2:驾驶员提前撤销
 * 3:平台公司撤销
 * 4;乘客违约撤销
 * 5:驾驶员违约撤销
 */
public enum OrderCancelEnum {
    CANCEL_PASSENGER_BEFORE(1),

    CANCEL_DRIVER_BEFORE(2),

    CANCEL_PLATFORM_BEFORE(3),

    CANCEL_PASSENGER_ILLEGAL(4),

    CANCEL_DRIVER_ILLEGAL(5);

    private final int code;

    OrderCancelEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static OrderCancelEnum get(int status) {
        for (OrderCancelEnum value : values()) {
            if (value.getCode() == status) {
                return value;
            }
        }
        return null;
    }

}
