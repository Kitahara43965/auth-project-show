package com.example.myauthapp.auth.token.entity;

import com.example.myauthapp.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "token_hash")
    }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Refresh Token自体ではなくハッシュ値を保存
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "refresh_token_family_id", nullable = false)
    private RefreshTokenFamily refreshTokenFamily;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected RefreshToken() {
    }

    public RefreshToken(
        RefreshTokenFamily refreshTokenFamily,
        String tokenHash, 
        LocalDateTime expiresAt
    ) {
        this.refreshTokenFamily = refreshTokenFamily;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    @PrePersist
    protected void onCreate() {
        if(this.createdAt == null){
            this.createdAt = LocalDateTime.now();
        }//this.createdAt
    }

    // ===== Getter =====

    public Long getId() {
        return id;
    }

    public RefreshTokenFamily getRefreshTokenFamily() {
        return refreshTokenFamily;
    }

    public User getUser() {
        return refreshTokenFamily.getUser();
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAtForTest(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    // ===== Business Method =====

    public void revoke() {
        if(!this.revoked){
            this.revoked = true;
            this.revokedAt = LocalDateTime.now();
        }
    }

    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now());
    }
}