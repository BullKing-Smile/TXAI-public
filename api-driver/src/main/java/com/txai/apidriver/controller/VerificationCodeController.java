package com.txai.apidriver.controller;

import com.txai.apidriver.request.VerificationCodeDTO;
import com.txai.apidriver.service.VerificationCodeService;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class VerificationCodeController {


    private final VerificationCodeService verificationCodeService;

    public VerificationCodeController(
            VerificationCodeService verificationCodeService
    ) {
        this.verificationCodeService = verificationCodeService;
    }

    @GetMapping("/verification-code")
    public ResponseResult<NumberCodeResponse> verificationCode(@Validated @RequestBody VerificationCodeDTO verificationCodeDTO) {
        String phone = verificationCodeDTO.getDriverPhone();
        int size = verificationCodeDTO.getSize();
        System.out.println("received phone number is:" + phone + ", and size is:" + size);
        return verificationCodeService.generateCode(phone, size);
    }

    /**
     * Login API
     *
     * @param verificationCodeCheckDTO verification code entity
     * @return refresh token and access token
     */
    @PostMapping("/verification-code-check")
    public ResponseResult verificationCodeCheck(@RequestBody VerificationCodeCheckDTO verificationCodeCheckDTO) {
        return verificationCodeService.verificationCodeCheck(verificationCodeCheckDTO);
    }
}
