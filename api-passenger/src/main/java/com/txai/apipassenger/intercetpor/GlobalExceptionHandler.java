package com.txai.apipassenger.intercetpor;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.ResponseResult;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理类
 */
@RestControllerAdvice
@Order(99)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e) {

        e.printStackTrace();
        return ResponseResult.fail(CommonStatusEnum.FAIL);
    }
}
