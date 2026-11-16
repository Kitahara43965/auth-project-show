package com.example.myauthapp.token.tokenhash.service;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.example.myauthapp.token.tokenhash.error.TokenHashTokenError;
import com.example.myauthapp.web.token.exception.TokenException;

@Service
public class TokenHashService {
    public String generateTokenHash(String stringToken) {
        MessageDigest messageDigest = null;
        byte[] byteHashes = null;
        byte byteHash = (byte)0;
        int maxByteNumber = 0;
        int byteNumber = 0;
        StringBuilder stringBuilderTokenHash = null;
        String stringTokenHash = null;
        String stringIntegerTokenHash = null;
        TokenHashTokenError tokenHashTokenError = null;

        try {
            if(stringToken == null){
                if(tokenHashTokenError == null){
                    tokenHashTokenError = TokenHashTokenError.TOKEN_HASH_STRING_TOKEN_IS_NULL;
                }//tokenHashTokenError
            }else{//stringToken
                messageDigest = MessageDigest.getInstance("SHA-256");
            }//stringToken
        } catch (NoSuchAlgorithmException e) {
            if(tokenHashTokenError == null){
                tokenHashTokenError = TokenHashTokenError.TOKEN_HASH_HASH_ALGORITHM_NOT_SUPPORTED;
            }//tokenHashTokenError
        }
        
        if(tokenHashTokenError == null){
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
        }//tokenHashTokenError&null

        if(tokenHashTokenError == null){
            return stringTokenHash;
        }else{//tokenHashTokenError
            throw new TokenException(tokenHashTokenError);
        }//tokenHashTokenError

    }//generateTokenHash
}