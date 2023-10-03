package dev.lukasnehrke.vulnaware.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtHandler {

    @Value("${vulnaware.security.secret}")
    private String jwtSecret;

    public String createToken(final Authentication auth) {
        final UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        return JWT.create().withSubject(user.getUsername()).sign(Algorithm.HMAC256(jwtSecret));
    }

    public DecodedJWT decodeToken(final String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token);
    }
}
