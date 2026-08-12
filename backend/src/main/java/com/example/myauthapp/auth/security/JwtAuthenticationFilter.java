package com.example.myauthapp.auth.security;

import com.example.myauthapp.auth.service.AccessTokenJwtService;
import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.user.repository.UserRepository;
import com.example.myauthapp.auth.cookie.enums.CookieType;
import com.example.myauthapp.auth.cookie.reader.CookieReader;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenJwtService accessTokenJwtService;
    private final UserRepository userRepository;
    private final CookieReader cookieReader;


    public JwtAuthenticationFilter(
            AccessTokenJwtService accessTokenJwtService,
            UserRepository userRepository,
            CookieReader cookieReader
    ) {
        this.accessTokenJwtService = accessTokenJwtService;
        this.userRepository = userRepository;
        this.cookieReader = cookieReader;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/api/login")
            || path.equals("/api/logout")
            || path.equals("/api/register")
            || path.equals("/api/refresh");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String token = cookieReader.getTokenFromCookie(request,CookieType.ACCESS_TOKEN);
        String email = null;
        boolean isValid = false;
        Optional<User> optionalUser = null;
        User user = null;
        Long userId = null;
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

        isValid = token != null && accessTokenJwtService.isValid(token);

        if (isValid) {

            email = accessTokenJwtService.extractEmail(token);
            if(email != null){
                optionalUser = userRepository.findByEmail(email);
            }//email

            if(optionalUser != null && optionalUser.isPresent()){

                user = optionalUser.get();
                userId = user.getId();

                usernamePasswordAuthenticationToken = 
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of()
                    );

                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    SecurityContextHolder
                        .getContext()
                        .setAuthentication(usernamePasswordAuthenticationToken);
                }//SecurityContextHolder.getContext().getAuthentication()

            }//optionalUser
        }

        chain.doFilter(request, response);
    }


    
}