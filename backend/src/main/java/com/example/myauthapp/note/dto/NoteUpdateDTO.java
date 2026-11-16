package com.example.myauthapp.note.dto;

public class NoteUpdateDTO {

    private final String title;
    private final String content;
    private final Long version;

    public NoteUpdateDTO(
        String title,
        String content,
        Long version
    ) {
        this.title = title;
        this.content = content;
        this.version = version;
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
}