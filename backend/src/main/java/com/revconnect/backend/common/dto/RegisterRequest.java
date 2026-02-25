package com.revconnect.backend.common.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String email;
    private String password;

    private String passwordHint;
    private String securityQuestion;
    private String securityAnswer;
}