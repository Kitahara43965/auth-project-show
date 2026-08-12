package com.example.myauthapp.auth.token.service;

import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import com.example.myauthapp.auth.token.repository.RefreshTokenFamilyRepository;
import com.example.myauthapp.user.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenFamilyService {

    private final RefreshTokenFamilyRepository refreshTokenFamilyRepository;

    @Transactional
    public RefreshTokenFamily createRefreshTokenFamily(User user) {

        RefreshTokenFamily refreshTokenFamily =
            new RefreshTokenFamily(user);

        refreshTokenFamilyRepository.save(refreshTokenFamily);

        return refreshTokenFamily;
    }

    @Transactional
    public void revokeRefreshTokenFamily(RefreshTokenFamily refreshTokenFamily) {
        refreshTokenFamily.revoke();
        refreshTokenFamilyRepository.save(refreshTokenFamily);
    }
}