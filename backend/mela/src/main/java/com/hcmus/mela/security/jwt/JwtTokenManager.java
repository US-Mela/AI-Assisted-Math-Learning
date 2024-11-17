package com.hcmus.mela.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hcmus.mela.model.postgre.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    private final JwtProperties jwtProperties;

    private Algorithm getAlgorithm() {
        String secretKey = jwtProperties.getSecretKey();
        if (secretKey == null) throw new IllegalStateException("Secret key is not configured");
        return Algorithm.HMAC256(secretKey.getBytes());
    }

    private String generateToken(User user, long expirationTimeMs) {
        return JWT.create()
                .withSubject(user.getUserId().toString())
                .withIssuer(jwtProperties.getIssuer())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getUserRole().name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeMs))
                .sign(getAlgorithm());
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtProperties.getAccessTokenExpirationMinute() * 60 * 1000);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtProperties.getRefreshTokenExpirationDay() * 24 * 60 * 60 * 1000);
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getDecodedJWT(token).getSubject());
    }

    public String getUsernameFromToken(String token) {
        return getDecodedJWT(token).getClaim("username").asString();
    }

    public boolean validateToken(String token, String authenticatedUsername) {
        DecodedJWT decodedJWT = getDecodedJWT(token);
        return authenticatedUsername.equals(decodedJWT.getClaim("username").asString())
                && !decodedJWT.getExpiresAt().before(new Date());
    }

    private DecodedJWT getDecodedJWT(String token) {
        try {
            return JWT.require(getAlgorithm()).build().verify(token);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }
}
