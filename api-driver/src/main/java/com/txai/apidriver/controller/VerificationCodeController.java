package com.txai.apidriver.controller;

import com.txai.apidriver.service.VerificationCodeService;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class VerificationCodeController {


    private final VerificationCodeService verificationCodeService;

    public VerificationCodeController(
            VerificationCodeService verificationCodeService
    ) {
        this.verificationCodeService = verificationCodeService;
    }

    @GetMapping("/verification-code/{driverPhone}")
    public ResponseResult<NumberCodeResponse> verificationCode(@PathVariable("driverPhone") String driverPhone) {
        System.out.println("received phone number is:" + driverPhone);
        return verificationCodeService.generateCode(driverPhone);
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
