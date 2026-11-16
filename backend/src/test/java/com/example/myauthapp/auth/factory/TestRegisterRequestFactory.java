package com.example.myauthapp.auth.factory;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.user.dto.UserCreateDTO;
import com.example.myauthapp.user.factory.TestUserDTOFactory;
import org.springframework.stereotype.Component;

@Component
public class TestRegisterRequestFactory {

    private final TestUserDTOFactory testUserDTOFactory;

    public TestRegisterRequestFactory(
        TestUserDTOFactory testUserDTOFactory
    ) {
        this.testUserDTOFactory = testUserDTOFactory;
    }

    public RegisterRequest create() {

        UserCreateDTO userCreateDTO =
            testUserDTOFactory.getUserCreateDTO();

        return new RegisterRequest(
            userCreateDTO.getEmail(),
            userCreateDTO.getPassword(),
            userCreateDTO.getName(),
            userCreateDTO.getPassword()
        );
    }
}