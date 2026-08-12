package com.example.myauthapp.audit.event;

public enum AuditEventType {
    LOGIN,
    LOGOUT,
    REGISTER,
    PASSWORD_CHANGED,
    EMAIL_VERIFICATION_SENT,
    EMAIL_VERIFIED,
    REFRESH_TOKEN_ISSUED,
    REFRESH_TOKEN_REVOKED,
    REFRESH_TOKEN_ROTATED
}