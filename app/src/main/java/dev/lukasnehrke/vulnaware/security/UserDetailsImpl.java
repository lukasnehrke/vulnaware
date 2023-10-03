package dev.lukasnehrke.vulnaware.security;

import dev.lukasnehrke.vulnaware.user.model.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.StringJoiner;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;

    public static UserDetailsImpl of(final User user) {
        return new UserDetailsImpl(user.getId(), user.getEmail(), user.getPassword());
    }

    @Override
    public String getUsername() {
        /* email acts as username */
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /* not implemented */
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        /* not implemented */
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        /* not implemented */
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        /* not implemented */
        return true;
    }

    @Override
    public boolean isEnabled() {
        /* not implemented */
        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserDetailsImpl.class.getSimpleName() + "[", "]").add("id=" + id).add("email='" + email + "'").toString();
    }
}
