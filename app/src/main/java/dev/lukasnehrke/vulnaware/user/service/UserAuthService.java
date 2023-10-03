package dev.lukasnehrke.vulnaware.user.service;

import dev.lukasnehrke.vulnaware.notification.model.type.WelcomeNotification;
import dev.lukasnehrke.vulnaware.notification.repository.NotificationRepository;
import dev.lukasnehrke.vulnaware.notification.service.NotificationService;
import dev.lukasnehrke.vulnaware.security.JwtHandler;
import dev.lukasnehrke.vulnaware.user.dto.RegisterRequest;
import dev.lukasnehrke.vulnaware.user.error.UserAlreadyExistsProblem;
import dev.lukasnehrke.vulnaware.user.model.User;
import dev.lukasnehrke.vulnaware.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Profile("default")
public class UserAuthService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);
    private final UserRepository users;
    private final NotificationService notificationService;
    private final AuthenticationManager authManager;
    private final JwtHandler jwt;
    private final PasswordEncoder encoder;

    @Transactional
    public User createUser(final RegisterRequest req) {
        if (users.findByEmail(req.getEmail()).isPresent()) {
            throw new UserAlreadyExistsProblem();
        }

        var user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

        user = users.save(user);
        logger.info("Created user '{}' with email {}", user.getId(), user.getEmail());

        final var notification = new WelcomeNotification();
        notification.setUser(user);
        notificationService.save(notification);

        return user;
    }

    public String authenticateUser(final String email, final String password) {
        logger.info("Authenticating user with email '{}'", email);
        final var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return jwt.createToken(auth);
    }
}
