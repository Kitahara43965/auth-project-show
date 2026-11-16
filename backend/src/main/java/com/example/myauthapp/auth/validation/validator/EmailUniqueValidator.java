package com.example.myauthapp.auth.validation.validator;

import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.auth.validation.annotation.EmailUnique;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailUniqueValidator 
        implements ConstraintValidator<EmailUnique, String> {

    private final UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;

    public EmailUniqueValidator(UserDiscoveryJdbcRepository userDiscoveryJdbcRepository) {
        this.userDiscoveryJdbcRepository = userDiscoveryJdbcRepository;
    }


    @Override
    public boolean isValid(
        String email,
        ConstraintValidatorContext constraintValidatorContext
    ) {
        boolean hasValidationError = false;
        boolean doesUserExistByEmail 
            = userDiscoveryJdbcRepository.doesUserExistByEmail(email);

        if (email == null || email.isBlank()) {
            hasValidationError = true;
        }else{//email
            if(doesUserExistByEmail == false){
                hasValidationError = true;
            }else if(doesUserExistByEmail == true){
                hasValidationError = false;
            }//doesUserExistByEmail
        }//email

        return hasValidationError;
    }
}