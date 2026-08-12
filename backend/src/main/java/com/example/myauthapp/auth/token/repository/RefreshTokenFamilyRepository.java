package com.example.myauthapp.auth.token.repository;

import com.example.myauthapp.auth.token.entity.RefreshTokenFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenFamilyRepository
        extends JpaRepository<RefreshTokenFamily, UUID> {
}