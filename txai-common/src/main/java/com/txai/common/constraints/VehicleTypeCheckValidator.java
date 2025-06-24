package com.txai.common.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class VehicleTypeCheckValidator implements ConstraintValidator<VehicleTypeCheck, String> {

    private List<String> vehicleTypeList = null;

    @Override
    public void initialize(VehicleTypeCheck constraintAnnotation) {
        vehicleTypeList = Arrays.asList(constraintAnnotation.vehicleTypeValue());
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        log.info(s);
        return vehicleTypeList.contains(s);
    }
}
