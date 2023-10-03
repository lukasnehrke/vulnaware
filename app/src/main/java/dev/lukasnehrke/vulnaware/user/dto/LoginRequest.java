package dev.lukasnehrke.vulnaware.user.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;

    private String password;
}
