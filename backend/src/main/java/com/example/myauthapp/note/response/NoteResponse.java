package com.example.myauthapp.note.response;

import java.time.LocalDateTime;
import com.example.myauthapp.note.dto.NoteDTO;

public class NoteResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final Long version;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public NoteResponse(NoteDTO dto) {
        this.id = dto.getId();
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.version = dto.getVersion();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
    }

    public Long getId() {
        return id;
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