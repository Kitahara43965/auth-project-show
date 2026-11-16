package com.example.myauthapp.user.service;

import com.example.myauthapp.user.dto.UserResponse;
import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.user.repository.insert.UserInsertJdbcRepository;
import com.example.myauthapp.user.error.MeUserError;
import com.example.myauthapp.web.user.exception.UserException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class UserService {

    private final UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;
    private final UserInsertJdbcRepository userInsertJdbcRepository;

    public UserService(
        UserDiscoveryJdbcRepository userDiscoveryJdbcRepository,
        UserInsertJdbcRepository userInsertJdbcRepository
    ) {
        this.userDiscoveryJdbcRepository = userDiscoveryJdbcRepository;
        this.userInsertJdbcRepository = userInsertJdbcRepository;
    }


    public UserResponse me(
        Authentication authentication
    ) {
        Long userId =
            (Long) authentication.getPrincipal();
            
        Optional<UserDTO> optionalUserDTO =
            userDiscoveryJdbcRepository.findOptionalUserDTOById(userId);

        UserDTO userDTO = 
            userDiscoveryJdbcRepository.findUserDTOByOptionalUserDTO(
                optionalUserDTO
            );

        return new UserResponse(
            userDTO.getId(),
            userDTO.getName(),
            userDTO.getEmail()
        );
    }
}
