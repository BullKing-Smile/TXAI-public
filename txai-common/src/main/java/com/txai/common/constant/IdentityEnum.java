package com.txai.common.constant;

public enum IdentityEnum {

    Passenger("1", "passenger"), Driver("2", "driver");

    private String id;
    private String value;

    IdentityEnum(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
