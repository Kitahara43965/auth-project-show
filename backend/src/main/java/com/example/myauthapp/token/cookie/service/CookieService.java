package com.example.myauthapp.token.cookie.service;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

import com.example.myauthapp.token.cookie.config.CookieProperties;
import com.example.myauthapp.token.cookie.error.CookieError;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.cookie.enums.CookieOperation;
import com.example.myauthapp.token.cookie.enums.CookieType;


@Service
public class CookieService {

    private final CookieProperties cookieProperties;

    public CookieService(
            CookieProperties cookieProperties
    ) {
        this.cookieProperties = cookieProperties;
    }

    private Duration getDurationMaxAge(
        CookieType cookieType
    ){
        Duration durationMaxAge = Duration.ZERO;

        if(cookieType == CookieType.ACCESS_TOKEN){
            durationMaxAge = Duration.ofMillis(
                cookieProperties.getAccessTokenMaxAge()
            );
        }else if(cookieType == CookieType.REFRESH_TOKEN){
            durationMaxAge = Duration.ofMillis(
                cookieProperties.getRefreshTokenMaxAge()
            );
        }//cookieType

        return durationMaxAge;

    }

    public CookieError operateTokenCookie(
        HttpServletResponse response,
        String token,
        CookieType cookieType,
        CookieOperation cookieOperation
    ){
        CookieError cookieError = null;
        Duration durationMaxAge = Duration.ZERO;
        ResponseCookie responseCookie = null;
        int nonNullCookieOperationMarker = 0;

        if(cookieType == null || cookieType.getName() == null){
                if(cookieError == null){
                        cookieError = CookieError.COOKIE_CONFIGURATION_ERROR;
                }//cookieError
        }//cookieType.getName()

        if(cookieOperation == null){
                if(cookieError == null){
                        cookieError = CookieError.COOKIE_OPERATION_ERROR;
                }//cookieError
        }

        nonNullCookieOperationMarker = 0;
        if(cookieOperation != null){
                if(cookieOperation == CookieOperation.ADD){
                        nonNullCookieOperationMarker = 1;
                }//cookieOperation
        }//cookieOperation

        if(nonNullCookieOperationMarker != 0){
                if(token == null){
                        if(cookieError == null){
                                cookieError = CookieError.COOKIE_VALUE_ERROR;
                        }//cookieError
                }//token
        }//nonNullCookieOperationMarker&0

        if(cookieError == null){
                if(cookieOperation == CookieOperation.ADD){
                        durationMaxAge = getDurationMaxAge(cookieType);
                }else if(cookieOperation == CookieOperation.DELETE){
                        durationMaxAge = Duration.ZERO;
                }//cookieOperation

                responseCookie = ResponseCookie.from(
                                cookieType.getName(),
                                token
                        )
                        .httpOnly(true)
                        .secure(cookieProperties.isSecure())
                        .sameSite(cookieProperties.getSameSite())
                        .path("/")
                        .maxAge(durationMaxAge)
                        .build();


                response.addHeader(
                        HttpHeaders.SET_COOKIE,
                        responseCookie.toString()
                );
                
        }//cookieError

        return cookieError;

    }

    public void addTokenCookie(
            HttpServletResponse response,
            String token,
            CookieType cookieType
    ) {
        CookieError cookieError = operateTokenCookie(
                response,
                token,
                cookieType,
                CookieOperation.ADD
        );

        if(cookieError != null){
                throw new TokenException(cookieError);
        }//cookieError
    }

    public void deleteTokenCookie(
            HttpServletResponse response,
            CookieType cookieType
    ) {
        String token = "";
        CookieError cookieError = operateTokenCookie(
                response,
                token,
                cookieType,
                CookieOperation.DELETE
        );

        if(cookieError != null){
                throw new TokenException(cookieError);
        }//cookieError
    }

}