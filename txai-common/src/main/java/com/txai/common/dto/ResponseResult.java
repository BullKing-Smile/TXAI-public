package com.txai.common.dto;

import com.txai.common.constant.CommonStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponseResult<T> {

    private int code;
    private String message;
    private T data;


    public static ResponseResult success() {
        return new ResponseResult<>().setCode(CommonStatusEnum.SUCCESS.getCode())
                .setMessage(CommonStatusEnum.SUCCESS.getValue());
    }

    public static <T> ResponseResult success(T data) {
        return new ResponseResult<>().setCode(CommonStatusEnum.SUCCESS.getCode())
                .setMessage(CommonStatusEnum.SUCCESS.getValue())
                .setData(data);
    }

    public static ResponseResult fail() {
        return new ResponseResult<>().setCode(CommonStatusEnum.FAIL.getCode())
                .setMessage(CommonStatusEnum.FAIL.getValue());
    }

}
