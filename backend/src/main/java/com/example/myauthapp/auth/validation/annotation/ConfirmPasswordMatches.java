package com.example.myauthapp.auth.validation.annotation;

import com.example.myauthapp.auth.validation.validator.ConfirmPasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfirmPasswordMatchesValidator.class)
public @interface ConfirmPasswordMatches {

    String message() default "パスワードが一致しません";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}