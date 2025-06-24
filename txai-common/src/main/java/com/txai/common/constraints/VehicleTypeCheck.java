package com.txai.common.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VehicleTypeCheckValidator.class)
public @interface VehicleTypeCheck {

    String message() default "The vehicle type is invalid";

    String[] vehicleTypeValue() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
