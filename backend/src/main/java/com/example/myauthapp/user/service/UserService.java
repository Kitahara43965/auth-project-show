package com.example.myauthapp.user.service;

import com.example.myauthapp.user.dto.UserResponse;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.user.error.UserError;
import com.example.myauthapp.user.exception.UserException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }


    public UserResponse me(
            Authentication authentication
    ) {
        Long userId =
            (Long) authentication.getPrincipal();
        Optional<User> optionalUser =
            userRepository.findById(userId);
        User user = null;
        UserError userError = null;
        String userName = null;
        String userEmail = null;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            userName = user.getName();
            userEmail = user.getEmail();
        } else {
            userError = UserError.USER_NOT_FOUND;
        }

        if(userError == null){
            return new UserResponse(
                userId,
                userName,
                userEmail
            );
        }else{//userError&null
            throw new UserException(userError);
        }//userError&null
    }
}
