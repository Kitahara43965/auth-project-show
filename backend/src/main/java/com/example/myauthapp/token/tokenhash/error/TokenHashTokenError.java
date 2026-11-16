package com.example.myauthapp.token.tokenhash.error;

import com.example.myauthapp.web.token.error.TokenError;

public enum TokenHashTokenError implements TokenError{
    
    TOKEN_HASH_STRING_TOKEN_IS_NULL(
        "TOKEN_HASH_STRING_TOKEN_IS_NULL",
        "Stringのトークンが存在しません"
    ),

    TOKEN_HASH_HASH_ALGORITHM_NOT_SUPPORTED(
        "TOKEN_HASH_HASH_ALGORITHM_NOT_SUPPORTED",
        "ハッシュアルゴリズムがサポートされていません"
    ),
    
    TOKEN_HASH_MUST_BE_A_64_CHARACTER_HEXADECIMAL_STRING(
        "TOKEN_HASH_MUST_BE_A_64_CHARACTER_HEXADECIMAL_STRING",
        "Error: Token hash must be a 64 character hexadecimal string."
    );

    TokenHashTokenError(
        String errorCode,
        String errorMessage
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private final String errorCode;
    private final String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}