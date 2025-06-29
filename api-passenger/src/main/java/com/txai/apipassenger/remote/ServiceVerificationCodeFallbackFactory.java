package com.txai.apipassenger.remote;

import com.txai.common.dto.ResponseResult;
import com.txai.common.response.NumberCodeResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ServiceVerificationCodeFallbackFactory implements FallbackFactory<ServiceVerficationCodeClient> {
    @Override
    public ServiceVerficationCodeClient create(Throwable cause) {
        return new ServiceVerficationCodeClient() {
            @Override
            public ResponseResult<NumberCodeResponse> numberCode(int size) {
                NumberCodeResponse numberCodeResponse = new NumberCodeResponse();
                numberCodeResponse.setNumberCode(0000);
                return ResponseResult.success(numberCodeResponse);
            }
        };
    }
}
