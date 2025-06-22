package com.txai.common.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PriceRuleIsNewRequest implements Serializable {

    private String fareType;

    private Integer fareVersion;
}
