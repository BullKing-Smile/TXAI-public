package com.txai.common.constant;

/**
 * 订单状态
 * 1：订单开始
 * 2：司机接单
 * 3：去接乘客
 * 4：司机到达乘客起点
 * 5：乘客上车，司机开始行程
 * 6：到达目的地，行程结束，未支付
 * 7：发起收款
 * 8: 支付完成
 * 9.订单取消'
 */
public enum OrderStatusEnum {
    INVALID(0),
    START(1),

    DRIVER_RECEIVE_ORDER(2),

    DRIVER_TO_PICKUP(3),

    DRIVER_ARRIVED_DEPARTURE(4),

    PASSENGER_GETON(5),

    PASSENGER_GETOFF(6),

    UNPAID(7),

    PAID(8),

    CANCELLED(9);

    private final int code;

    OrderStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static OrderStatusEnum get(int status) {
        for (OrderStatusEnum value : values()) {
            if (value.getCode() == status) {
                return value;
            }
        }
        return null;
    }

}
