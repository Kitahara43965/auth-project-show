package com.example.myauthapp.note.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NoteCreateRequest {

    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 200, message = "タイトルは200文字以内で入力してください")
    private String title;

    @NotBlank(message = "本文を入力してください")
    private String content;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}