package com.example.myauthapp.auth.token.service;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.example.myauthapp.auth.token.error.TokenHashError;
import com.example.myauthapp.auth.token.exception.TokenHashException;

@Service
public class TokenHashService {
    public String createTokenHash(String stringToken) {
        MessageDigest messageDigest = null;
        byte[] byteHashes = null;
        byte byteHash = (byte)0;
        int maxByteNumber = 0;
        int byteNumber = 0;
        StringBuilder stringBuilderTokenHash = null;
        String stringTokenHash = null;
        String stringIntegerTokenHash = null;
        TokenHashError tokenHashError = null;

        try {
            if(stringToken == null){
                if(tokenHashError == null){
                    tokenHashError = TokenHashError.STRING_TOKEN_IS_NULL;
                }//tokenHashError
            }else{//stringToken
                messageDigest = MessageDigest.getInstance("SHA-256");
            }//stringToken
        } catch (NoSuchAlgorithmException e) {
            if(tokenHashError == null){
                tokenHashError = TokenHashError.HASH_ALGORITHM_NOT_SUPPORTED;
            }//tokenHashError
        }
        
        if(tokenHashError == null){
            byteHashes = messageDigest.digest(stringToken.getBytes(StandardCharsets.UTF_8));
            stringBuilderTokenHash = new StringBuilder();

            maxByteNumber = 0;
            if(byteHashes != null){
                maxByteNumber = byteHashes.length;
            }//byteHashes

            for(byteNumber = 1;byteNumber <= maxByteNumber;byteNumber++){
                byteHash = byteHashes[byteNumber - 1];
                stringIntegerTokenHash = Integer.toHexString(0xff & byteHash);
                if(stringIntegerTokenHash.length() == 1){
                    stringBuilderTokenHash.append('0');
                }
                stringBuilderTokenHash.append(stringIntegerTokenHash);
            }//byteNumber

            stringTokenHash = stringBuilderTokenHash.toString();
        }//tokenHashError&null

        if(tokenHashError == null){
            return stringTokenHash;
        }else{//tokenHashError
            throw new TokenHashException(tokenHashError);
        }//tokenHashError

    }//createTokenHash
}