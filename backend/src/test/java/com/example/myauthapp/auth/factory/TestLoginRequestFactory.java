package com.example.myauthapp.auth.factory;

import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.user.dto.UserCreateDTO;
import com.example.myauthapp.user.factory.TestUserDTOFactory;
import org.springframework.stereotype.Component;

@Component
public class TestLoginRequestFactory {

    private final TestUserDTOFactory testUserDTOFactory;

    public TestLoginRequestFactory(
        TestUserDTOFactory testUserDTOFactory
    ) {
        this.testUserDTOFactory = testUserDTOFactory;
    }

    public LoginRequest create() {

        UserCreateDTO userCreateDTO =
            testUserDTOFactory.getUserCreateDTO();

        return new LoginRequest(
            userCreateDTO.getEmail(),
            userCreateDTO.getPassword()
        );
    }
}