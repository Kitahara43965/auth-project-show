package com.example.myauthapp.note.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class NoteUpdateRequest {

    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 200, message = "タイトルは200文字以内で入力してください")
    private String title;

    @NotBlank(message = "本文を入力してください")
    private String content;

    @NotNull(message = "バージョンを指定してください")
    @PositiveOrZero(message = "バージョンは0以上で指定してください")
    private Long version;

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