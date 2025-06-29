package com.txai.apidriver.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCodeDTO {

    @NotBlank
    @Pattern(regexp = "^1[1-9][0-9]{9}", message = "手机号码格式不正确")
    private String driverPhone;
    @Min(4)
    @Max(8)
    @NotNull
    private Integer size;
}
