package com.example.myauthapp.support.factory;

import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.example.myauthapp.support.dto.TestUserData;
import com.example.myauthapp.support.util.TestUserUtils;

@Component
public class TestUserFactory {

    private final UserRepository userRepository;

    public TestUserFactory(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public TestUserData getTestUserData() {

        UUID uuid = UUID.randomUUID();

        TestUserData testUserData = new TestUserData(
            TestUserUtils.getEmail(uuid),
            TestUserUtils.getPassword(uuid),
            TestUserUtils.getName(uuid)
        );

        return testUserData;
    }

    public User testCreateUser() {
        TestUserData testUserData = getTestUserData();
        
        User user = new User(
            testUserData.email(),
            testUserData.password(),
            testUserData.name()
        );
        return userRepository.save(user);
    }

    
}