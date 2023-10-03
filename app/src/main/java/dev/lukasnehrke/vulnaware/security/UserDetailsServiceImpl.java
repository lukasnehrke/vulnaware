package dev.lukasnehrke.vulnaware.security;

import dev.lukasnehrke.vulnaware.user.model.User;
import dev.lukasnehrke.vulnaware.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
        return UserDetailsImpl.of(user);
    }

    public UserDetails loadByApiKey(final String key) {
        final User user = userRepository.findByApiKey(key).orElseThrow(() -> new UsernameNotFoundException("Not found: " + key));
        return UserDetailsImpl.of(user);
    }
}
