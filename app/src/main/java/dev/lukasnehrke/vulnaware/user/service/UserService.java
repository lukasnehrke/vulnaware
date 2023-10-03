package dev.lukasnehrke.vulnaware.user.service;

import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.user.model.User;
import dev.lukasnehrke.vulnaware.user.repository.UserRepository;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final SecureRandom random = new SecureRandom();
    private final UserRepository users;

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(final String email) {
        return users.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getReference(final UserDetails details) {
        return users.getReferenceById(((UserDetailsImpl) details).getId());
    }

    @Transactional
    public String generateApiKey(final UserDetails details) {
        final User user = users.findById(((UserDetailsImpl) details).getId()).orElseThrow();

        final String apiKey = generateKey();
        user.setApiKey(apiKey);
        users.save(user);

        return apiKey;
    }

    private String generateKey() {
        final byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
