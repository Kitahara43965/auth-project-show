package com.example.myauthapp.token.cookie.helper;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import com.example.myauthapp.token.cookie.enums.CookieType;
import com.example.myauthapp.token.cookie.error.CookieError;
import com.example.myauthapp.web.token.exception.TokenException;

@Component
public class TestCookieHelper {

    public static Cookie getTokenCookie(
        MockHttpServletResponse mockHttpServletResponse,
        CookieType cookieType
    ) {
        Cookie cookie = null;
        CookieError cookieError = null;

        if(cookieType == null){
            cookieError = CookieError.COOKIE_TYPE_IS_NULL;
        }else{//cookieType
            cookie = Arrays.stream(mockHttpServletResponse.getCookies())
                .filter(comparedCookie ->
                    comparedCookie.getName().equals(
                        cookieType.getName()
                    )
                )
                .findFirst()
                .orElseThrow();
        }//cookieType

        if(cookieError == null){
            return cookie;
        }else{//cookieError
            throw new TokenException(cookieError);
        }//cookieError
    }
}
