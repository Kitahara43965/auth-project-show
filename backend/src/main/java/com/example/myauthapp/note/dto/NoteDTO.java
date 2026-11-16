package com.example.myauthapp.note.dto;

import java.time.LocalDateTime;

public class NoteDTO {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
    private final Long version;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public NoteDTO(
        Long id,
        Long userId,
        String title,
        String content,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}