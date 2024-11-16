package com.hcmus.mela.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hcmus.mela.model.postgre.User;
import com.hcmus.mela.model.postgre.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenManager {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user) {
        Long userID = user.getUserId();
        UserRole userRole = user.getUserRole();

        if (jwtProperties.getSecretKey() == null) {
            throw new IllegalStateException("Secret key is not configured");
        }

        //@formatter:off
		return JWT.create()
				.withSubject(userID.toString())
				.withIssuer(jwtProperties.getIssuer())
				.withClaim("role", userRole.name())
				.withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMinute() * 60 * 1000))
				.sign(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()));
		//@formatter:on
    }

    public String generateRefreshToken(User user) {
        Long userID = user.getUserId();
        final UserRole userRole = user.getUserRole();

        if (jwtProperties.getSecretKey() == null) {
            throw new IllegalStateException("Secret key is not configured");
        }

        //@formatter:off
        return JWT.create()
                .withSubject(userID.toString())
                .withIssuer(jwtProperties.getIssuer())
                .withClaim("role", userRole.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpirationDay() * 1000 * 60 * 60 * 24))
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes()));
        //@formatter:on
    }

    public String getUsernameFromToken(String token) {
        final DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token, String authenticatedUsername) {
        final String usernameFromToken = getUsernameFromToken(token);
        final boolean equalsUsername = usernameFromToken.equals(authenticatedUsername);
        final boolean tokenExpired = isTokenExpired(token);

        return equalsUsername && !tokenExpired;
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDateFromToken = getExpirationDateFromToken(token);
        return expirationDateFromToken.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        final DecodedJWT decodedJWT = getDecodedJWT(token);
        return decodedJWT.getExpiresAt();
    }

    private DecodedJWT getDecodedJWT(String token) {
        if (jwtProperties.getSecretKey() == null) {
            throw new IllegalStateException("Secret key is not configured");
        }

        final JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey().getBytes())).build();
        return jwtVerifier.verify(token);
    }
}