package com.txai.common.request;

import lombok.Data;

@Data
public class PushRequest {

    private Long userId;
    private String identity;
    private String content;

}
