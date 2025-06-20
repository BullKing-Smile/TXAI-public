package com.txai.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodeCheckDTO {
    private String passengerPhone;
    private String verificationCode;
}
