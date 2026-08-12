package com.example.myauthapp.support.factory;

import com.example.myauthapp.auth.dto.LoginRequest;
import com.example.myauthapp.support.dto.TestUserData;
import com.example.myauthapp.support.factory.TestUserFactory;
import org.springframework.stereotype.Component;

@Component
public class TestLoginRequestFactory {

    private final TestUserFactory testUserFactory;

    public TestLoginRequestFactory(
        TestUserFactory testUserFactory
    ) {
        this.testUserFactory = testUserFactory;
    }

    public LoginRequest create() {

        TestUserData testUserData =
            testUserFactory.getTestUserData();

        return new LoginRequest(
            testUserData.email(),
            testUserData.password()
        );
    }
}