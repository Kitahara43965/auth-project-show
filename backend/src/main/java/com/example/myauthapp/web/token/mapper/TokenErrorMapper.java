package com.example.myauthapp.web.token.mapper;

import org.springframework.http.HttpStatus;
import com.example.myauthapp.web.token.error.TokenError;
import com.example.myauthapp.token.jwttoken.error.JwtTokenError;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenFamilyTokenError;
import com.example.myauthapp.token.tokenhash.error.TokenHashTokenError;
import com.example.myauthapp.token.cookie.error.CookieError;

public final class TokenErrorMapper {

    private TokenErrorMapper() {
    }

    public static HttpStatus toHttpStatus(TokenError tokenError) {

        if (tokenError instanceof JwtTokenError error) {

            switch (error) {

                case JWT_TOKEN_NO_USER:
                    return HttpStatus.UNAUTHORIZED;

                case JWT_TOKEN_NO_EMAIL:
                    return HttpStatus.UNAUTHORIZED;

                case JWT_TOKEN_NO_TOKEN:
                    return HttpStatus.UNAUTHORIZED;

                default:
                    throw new IllegalArgumentException(
                        "Unknown JwtTokenError: " + error
                    );
            }
        }

        if (tokenError instanceof RefreshTokenTokenError error) {
            switch (error) {

                case REFRESH_TOKEN_DOES_NOT_EXIST:
                    return HttpStatus.UNAUTHORIZED;

                case STRING_REFRESH_TOKEN_DOES_NOT_EXIST:
                    return HttpStatus.UNAUTHORIZED;

                case REFRESH_TOKEN_IS_EXPIRED:
                    return HttpStatus.UNAUTHORIZED;

                case REFRESH_TOKEN_IS_REVOKED:
                    return HttpStatus.UNAUTHORIZED;

                case REFRESH_TOKEN_REUSE_IS_DETECTED:
                    return HttpStatus.UNAUTHORIZED;

                case REFRESH_TOKEN_HAS_NO_USER:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case NO_REFRESH_TOKENS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case FAILED_TO_SAVE_REFRESH_TOKEN:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown RefreshTokenTokenError: " + error
                    );
            }
        }

        if (tokenError instanceof RefreshTokenFamilyTokenError error) {
            switch (error) {

                case NO_REFRESH_TOKEN_FAMILIES_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case FAILED_TO_SAVE_REFRESH_TOKEN_FAMILY:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown RefreshTokenFamilyTokenError: " + error
                    );
            }
        }

        if (tokenError instanceof TokenHashTokenError error) {
            switch (error) {

                case TOKEN_HASH_STRING_TOKEN_IS_NULL:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case TOKEN_HASH_HASH_ALGORITHM_NOT_SUPPORTED:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case TOKEN_HASH_MUST_BE_A_64_CHARACTER_HEXADECIMAL_STRING:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown TokenHashTokenError: " + error
                    );
            }
        }

        if (tokenError instanceof CookieError error) {
            switch (error) {

                case COOKIE_CONFIGURATION_ERROR:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case COOKIE_OPERATION_ERROR:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case COOKIE_VALUE_ERROR:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                case COOKIE_TYPE_IS_NULL:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown CookieError: " + error
                    );
            }
        }

        throw new IllegalArgumentException(
            "Unknown TokenError: " + tokenError
        );
    }
    
}