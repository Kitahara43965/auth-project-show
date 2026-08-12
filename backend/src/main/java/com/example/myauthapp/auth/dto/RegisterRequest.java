package com.example.myauthapp.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.myauthapp.auth.validation.annotation.EmailUnique;
import com.example.myauthapp.auth.validation.annotation.ConfirmPasswordMatches;
import com.example.myauthapp.auth.validation.group.RequiredGroup;
import com.example.myauthapp.auth.validation.group.FormatGroup;
import com.example.myauthapp.auth.validation.group.CompareGroup;
import com.example.myauthapp.auth.validation.group.BusinessGroup;

@ConfirmPasswordMatches(groups = CompareGroup.class)
public class RegisterRequest {

    @NotBlank(
        groups = RequiredGroup.class,
        message = "ユーザー名は必須です"
    )
    @Size(
    max = 50,
        groups = FormatGroup.class,
        message = "ユーザー名は50文字以内で入力してください"
    )
    private String name;

    @NotBlank(
        groups = RequiredGroup.class,
        message = "メールアドレスは必須です"
    )
    @EmailUnique(
        groups = BusinessGroup.class
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
    @Size(
        min = 8,
        groups = FormatGroup.class,
        message = "パスワードは8文字以上で入力してください。"
    )
    private String password;

    @NotBlank(
        groups = RequiredGroup.class,
        message = "確認用パスワードは必須です"
    )
    private String confirmPassword;

    public RegisterRequest(
        String email,
        String password,
        String name,
        String confirmPassword
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.confirmPassword = confirmPassword;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}