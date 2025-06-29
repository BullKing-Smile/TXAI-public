package com.txai.apipassenger.remote;

import com.txai.common.dto.ResponseResult;
import com.txai.common.response.NumberCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "service-verificationcode", fallbackFactory = ServiceVerificationCodeFallbackFactory.class)
public interface ServiceVerficationCodeClient {
    @RequestMapping(method = RequestMethod.GET, value = "/numberCode/{size}")
    ResponseResult<NumberCodeResponse> numberCode(@PathVariable int size);
}
