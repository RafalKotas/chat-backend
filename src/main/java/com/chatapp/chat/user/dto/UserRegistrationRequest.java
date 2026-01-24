package com.chatapp.chat.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String firstName;
    private String lastName;
    private String username;
}
