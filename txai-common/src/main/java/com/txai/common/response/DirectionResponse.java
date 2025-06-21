package com.txai.common.response;

import lombok.Data;

@Data
public class DirectionResponse {
    // unit is m
    private Integer distance;
    // unit is s
    private Integer duration;
}
