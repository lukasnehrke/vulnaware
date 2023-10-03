package dev.lukasnehrke.vulnaware.user.controller;

import dev.lukasnehrke.vulnaware.user.dto.GenerateKeyResponse;
import dev.lukasnehrke.vulnaware.user.dto.LoginRequest;
import dev.lukasnehrke.vulnaware.user.dto.LoginResponse;
import dev.lukasnehrke.vulnaware.user.dto.RegisterRequest;
import dev.lukasnehrke.vulnaware.user.model.User;
import dev.lukasnehrke.vulnaware.user.service.UserAuthService;
import dev.lukasnehrke.vulnaware.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Profile("default")
public class AuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/login")
    ResponseEntity<?> register(@Valid @RequestBody final LoginRequest req) {
        final String jwt = userAuthService.authenticateUser(req.getEmail(), req.getPassword());

        final LoginResponse res = new LoginResponse();
        res.setJwt(jwt);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    ResponseEntity<?> register(@Valid @RequestBody final RegisterRequest req) {
        final User user = userAuthService.createUser(req);
        final String jwt = userAuthService.authenticateUser(user.getEmail(), req.getPassword());

        final LoginResponse res = new LoginResponse();
        res.setJwt(jwt);
        return ResponseEntity.ok(res);
    }
}
