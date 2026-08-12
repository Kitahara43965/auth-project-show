package com.example.myauthapp.auth.token.entity;

import com.example.myauthapp.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_token_families")
public class RefreshTokenFamily {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    protected RefreshTokenFamily() {
    }


    public RefreshTokenFamily(User user) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.revoked = false;
    }


    @PrePersist
    protected void onCreate() {
        if(this.createdAt == null){
            this.createdAt = LocalDateTime.now();
        }//this.createdAt
    }


    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }


    public void revoke() {
        if(!this.revoked){
            this.revoked = true;
            this.revokedAt = LocalDateTime.now();
        }
    }
}