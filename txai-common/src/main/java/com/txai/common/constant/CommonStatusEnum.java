package com.txai.common.constant;

public enum CommonStatusEnum {

    SUCCESS(1, "success"),
    FAIL(0, "fail"),
    ACCESS_TOKEN_ERROR(1098, "access token invalid"),
    REFRESH_TOKEN_ERROR(1099, "refresh token invalid"),
    USER_NOT_FOUND(2021, "user not found"),
    CALL_USER_ADD_ERROR(2022, "login or register error"),
    CHECK_CODE_ERROR(2023, "Check verification code error"),

    PRICE_RULE_EXISTS(4010, "price rule already exists"),
    PRICE_RULE_NOT_EXISTS(4011, "Price rule not exists"),
    PRICE_RULE_NOT_EDIT(4012, "price rule not edit"),
    PRICE_RULE_EMPTY(4013, "price rule empty"),
    DIC_DISTRICT_NOT_EXISTS(5010, "District not exists"),
    DRIVER_USER_NOT_EXISTS(6010, "Driver user not exists"),
    DRIVER_BIND_EXISTS(6011, "Driver bind exists"),
    CAR_BIND_EXISTS(6012, "Car bind exists"),
    DRIVER_CAR_BIND_NOT_EXISTS(6013, "Driver car bind not exists"),
    DRIVER_CAR_BIND_EXISTS(6014, "Driver car bind exists"),
    DRIVER_STATUS_UPDATE_ERROR(6015, "Driver status update failed"),
    CAR_NOT_EXISTS(6015, "Car not exists"),
    AVAILABLE_DRIVER_EMPTY(6016, "None driver available"),
    PRICE_RULE_CHANGED(6017, "Price rule changed"),
    CITY_SERVICE_NOT_SERVICE(6018, "No service running in this city"),
    ORDER_GOING_ON(6019, "Order is going on"),
    ORDER_CANCEL_ERROR(6020, "Order cancel error"),
    DEVICE_IS_BLACK(6021, "The device is black"),
    ORDER_UPDATE_ERROR(6022, "Order update error"),
    ORDER_NOT_EXISTS(6023, "Order not exists"),
    ORDER_CAN_NOT_GRAB(6024, "Order can't grab"),
    ORDER_GRABING(6025, "Order grabing"),
    VALIDATION_EXCEPTION(7001, "validation exception");

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
