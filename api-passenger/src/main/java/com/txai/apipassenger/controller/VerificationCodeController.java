package com.txai.apipassenger.controller;

import com.txai.apipassenger.request.VerificationCodeDTO;
import com.txai.apipassenger.service.VerificationCodeService;
import com.txai.common.dto.ResponseResult;
import com.txai.common.request.VerificationCodeCheckDTO;
import com.txai.common.response.NumberCodeResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//@Validated
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
        String phone = verificationCodeDTO.getPassengerPhone();
        int size = verificationCodeDTO.getSize();
        System.out.println("received phone number is:" + phone + ", and size is:" + size);
        return verificationCodeService.generateCode(phone, size);
    }

    @PostMapping("/verification-code-check")
    public ResponseResult verificationCodeCheck(@RequestBody VerificationCodeCheckDTO verificationCodeCheckDTO) {
        return verificationCodeService.verificationCodeCheck(verificationCodeCheckDTO);
    }
}
