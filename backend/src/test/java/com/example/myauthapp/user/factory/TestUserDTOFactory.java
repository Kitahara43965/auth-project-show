
package com.example.myauthapp.user.factory;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.auth.util.TestUserUtils;
import com.example.myauthapp.user.dto.UserCreateDTO;
import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.user.repository.insert.UserInsertJdbcRepository;

@Component
public class TestUserDTOFactory {

    private final UserInsertJdbcRepository userInsertJdbcRepository;

    public TestUserDTOFactory(
        UserInsertJdbcRepository userInsertJdbcRepository
    ) {
        this.userInsertJdbcRepository = userInsertJdbcRepository;
    }

    public UserCreateDTO getUserCreateDTO() {

        UUID uuid = UUID.randomUUID();

        return new UserCreateDTO(
            TestUserUtils.getEmail(uuid),
            TestUserUtils.getPassword(uuid),
            TestUserUtils.getName(uuid)
        );
    }

    public UserDTO create() {
        return userInsertJdbcRepository
            .insertUserByUserCreateDTO(getUserCreateDTO());
    }
}
