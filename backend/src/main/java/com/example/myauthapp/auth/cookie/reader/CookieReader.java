package com.example.myauthapp.auth.cookie.reader;

import com.example.myauthapp.auth.cookie.enums.CookieType;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieReader {
    public static String getTokenFromCookie(
        HttpServletRequest request,
        CookieType cookieType
    ) {
        Cookie[] cookies = request.getCookies();
        String cookieTypeName = cookieType.getName();
        String cookieValue = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if(cookieTypeName != null){
                    if (cookieTypeName.equals(cookie.getName())) {
                        cookieValue = cookie.getValue();
                    }
                }//cookieTypeName&null
            }
        }//cookies

        return cookieValue;
    }
}