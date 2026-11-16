package com.example.myauthapp.auth.security;

import com.example.myauthapp.token.jwttoken.service.AccessTokenJwtService;
import com.example.myauthapp.user.dto.UserDTO;
import com.example.myauthapp.user.repository.discovery.UserDiscoveryJdbcRepository;
import com.example.myauthapp.token.cookie.enums.CookieType;
import com.example.myauthapp.token.cookie.reader.CookieReader;

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
    private final UserDiscoveryJdbcRepository userDiscoveryJdbcRepository;
    private final CookieReader cookieReader;


    public JwtAuthenticationFilter(
        AccessTokenJwtService accessTokenJwtService,
        UserDiscoveryJdbcRepository userDiscoveryJdbcRepository,
        CookieReader cookieReader
    ) {
        this.accessTokenJwtService = accessTokenJwtService;
        this.userDiscoveryJdbcRepository = userDiscoveryJdbcRepository;
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

        String token =
            cookieReader.getTokenFromCookie(
                request,
                CookieType.ACCESS_TOKEN
            );

        Long userId = null;
        boolean isValid = false;
        Optional<UserDTO> optionalUserDTO = null;
        UserDTO userDTO = null;

        UsernamePasswordAuthenticationToken
            usernamePasswordAuthenticationToken = null;

        isValid =
            token != null
            && accessTokenJwtService.isValid(token);

        if (isValid) {

            userId =
                accessTokenJwtService.extractUserId(token);

            optionalUserDTO =
                userDiscoveryJdbcRepository.findOptionalUserDTOById(userId);

            if (optionalUserDTO.isPresent()) {

                userDTO = userDiscoveryJdbcRepository.findUserDTOByOptionalUserDTO(
                    optionalUserDTO
                );

                usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                        userDTO.getId(),
                        null,
                        List.of()
                    );

                if (
                    SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null
                ) {
                    SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                            usernamePasswordAuthenticationToken
                        );
                }
            }
        }

        chain.doFilter(request, response);
    }


    
}