
package com.example.myauthapp.note.dto;

public class NoteCreateDTO {

    private final String title;
    private final String content;

    public NoteCreateDTO(
        String title,
        String content
    ) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
