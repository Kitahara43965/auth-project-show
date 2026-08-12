package com.example.myauthapp.auth.validation.validator;

import java.util.Objects;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.auth.validation.annotation.ConfirmPasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class ConfirmPasswordMatchesValidator
        implements ConstraintValidator<ConfirmPasswordMatches, RegisterRequest> {

    @Override
    public boolean isValid(
            RegisterRequest registerRequest,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        boolean matches = false;

        if (registerRequest == null) {
            matches = true;
        }else{//registerRequest
            matches = Objects.equals(
                registerRequest.getPassword(),
                registerRequest.getConfirmPassword()
            );

            if (matches == false) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "パスワードが一致しません")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
            }//matches
        }//registerRequest

        return matches;
    }
}