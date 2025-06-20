package com.txai.common.constant;

public enum TokenTypeEnum {

    Access("1","access"),Refresh("2","refresh");

    private String id;
    private String value;

    TokenTypeEnum(String id, String value) {
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
