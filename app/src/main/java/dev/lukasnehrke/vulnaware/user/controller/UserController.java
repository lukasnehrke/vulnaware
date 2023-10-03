package dev.lukasnehrke.vulnaware.user.controller;

import dev.lukasnehrke.vulnaware.user.CurrentUser;
import dev.lukasnehrke.vulnaware.user.dto.GenerateKeyResponse;
import dev.lukasnehrke.vulnaware.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/generate-key")
    ResponseEntity<?> generateKey(@CurrentUser final UserDetails user) {
        final String apiKey = userService.generateApiKey(user);
        return ResponseEntity.ok(new GenerateKeyResponse(apiKey));
    }
}
