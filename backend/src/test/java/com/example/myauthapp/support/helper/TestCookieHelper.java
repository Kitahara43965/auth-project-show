package com.example.myauthapp.support.helper;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import com.example.myauthapp.auth.cookie.enums.CookieType;
import com.example.myauthapp.auth.cookie.error.CookieError;
import com.example.myauthapp.auth.cookie.exception.CookieException;

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
            throw new CookieException(cookieError);
        }//cookieError
    }
}
