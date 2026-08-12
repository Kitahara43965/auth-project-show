package com.example.myauthapp.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.myauthapp.auth.validation.group.RequiredGroup;
import com.example.myauthapp.auth.validation.group.FormatGroup;


public class LoginRequest {

    @NotBlank(
        groups = RequiredGroup.class,
        message = "メールアドレスは必須です"
    )
    @Email(
        groups = FormatGroup.class,
        message = "メールアドレスの形式が正しくありません"
    )
    private String email;

     @NotBlank(
        groups = RequiredGroup.class,
        message = "パスワードは必須です"
    )
    private String password;

    public LoginRequest(
        String email,
        String password
    ) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}