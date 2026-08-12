package com.example.myauthapp.support.factory;

import com.example.myauthapp.auth.dto.RegisterRequest;
import com.example.myauthapp.support.dto.TestUserData;
import com.example.myauthapp.support.factory.TestUserFactory;
import org.springframework.stereotype.Component;

@Component
public class TestRegisterRequestFactory {

    private final TestUserFactory testUserFactory;

    public TestRegisterRequestFactory(
        TestUserFactory testUserFactory
    ) {
        this.testUserFactory = testUserFactory;
    }

    public RegisterRequest create() {

        TestUserData testUserData =
            testUserFactory.getTestUserData();

        return new RegisterRequest(
            testUserData.email(),
            testUserData.password(),
            testUserData.name(),
            testUserData.password()
        );
    }
}