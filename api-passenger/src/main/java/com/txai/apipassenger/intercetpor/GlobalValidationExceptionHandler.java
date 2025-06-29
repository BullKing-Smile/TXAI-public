package com.txai.apipassenger.intercetpor;

import com.txai.common.constant.CommonStatusEnum;
import com.txai.common.dto.ResponseResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

/**
 * 校验异常处理类
 */
@RestControllerAdvice
@Order(1)
public class GlobalValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult validationExceptionHandler(MethodArgumentNotValidException e) {
        e.printStackTrace();
        return ResponseResult.fail(CommonStatusEnum.VALIDATION_EXCEPTION.getCode(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult ConstraintValidationExceptionHandler(ConstraintViolationException e) {
        e.printStackTrace();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String message = "";
        for (ConstraintViolation c : constraintViolations) {
            message = c.getMessage();
        }
        return ResponseResult.fail(CommonStatusEnum.VALIDATION_EXCEPTION.getCode(), message);
    }

}