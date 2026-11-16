package com.example.myauthapp.token.factory;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TestTokenDataFactory {
    public Long createTokenUserId() {
        return 200L;
    }
}