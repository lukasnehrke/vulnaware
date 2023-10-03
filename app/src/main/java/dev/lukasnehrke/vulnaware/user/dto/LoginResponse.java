package dev.lukasnehrke.vulnaware.user.dto;

import dev.lukasnehrke.vulnaware.user.model.User;
import lombok.Data;

@Data
public class LoginResponse {

    private String jwt;
    private User user;
}
