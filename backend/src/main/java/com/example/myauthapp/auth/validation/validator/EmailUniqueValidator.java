package com.example.myauthapp.auth.validation.validator;

import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.auth.validation.annotation.EmailUnique;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailUniqueValidator 
        implements ConstraintValidator<EmailUnique, String> {

    private final UserRepository userRepository;

    public EmailUniqueValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public boolean isValid(
            String email,
            ConstraintValidatorContext context
    ) {
        boolean hasValidationError = false;
        boolean existsByEmail = userRepository.existsByEmail(email);

        if (email == null || email.isBlank()) {
            hasValidationError = true;
        }else{//email
            if(existsByEmail == false){
                hasValidationError = true;
            }else if(existsByEmail == true){
                hasValidationError = false;
            }//existsByEmail
        }//email

        return hasValidationError;
    }
}