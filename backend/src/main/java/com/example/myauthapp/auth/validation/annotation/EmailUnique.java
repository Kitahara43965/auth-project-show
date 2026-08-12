package com.example.myauthapp.auth.validation.annotation;

import com.example.myauthapp.auth.validation.validator.EmailUniqueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailUniqueValidator.class)
public @interface EmailUnique {

    String message() default "このメールアドレスは既に登録されています";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}