package com.txai.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodeCheckDTO {
    @NotBlank
    @Pattern(regexp = "^1[1-9][0-9]{9}", message = "手机号码格式不正确")
    private String passengerPhone;
    @NotBlank(message = "验证码不为空")
    @Pattern(regexp = "\\d*")
    @Length(min = 4, max = 10)
    private String verificationCode;
}
