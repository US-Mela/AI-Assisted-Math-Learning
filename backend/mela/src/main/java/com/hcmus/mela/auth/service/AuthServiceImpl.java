package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.dto.request.*;
import com.hcmus.mela.auth.dto.response.*;
import com.hcmus.mela.auth.exception.InvalidTokenException;
import com.hcmus.mela.auth.exception.RegistrationException;
import com.hcmus.mela.auth.model.User;
import com.hcmus.mela.auth.model.UserRole;
import com.hcmus.mela.auth.repository.AuthRepository;
import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.auth.mapper.UserMapper;
import com.hcmus.mela.shared.cache.RedisService;
import com.hcmus.mela.shared.utils.ExceptionMessageAccessor;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REGISTRATION_SUCCESSFUL = "registration_successful";

    private static final String USERNAME_ALREADY_EXISTS = "username_already_exists";

    private final AuthRepository authRepository;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final GeneralMessageAccessor generalMessageAccessor;

    private final ExceptionMessageAccessor exceptionMessageAccessor;

    private final JwtTokenService jwtTokenService;

    private final RedisService redisService;

    @Override
    public User findByUsername(String username) {

        return authRepository.findByUsername(username);
    }

    @Override
    public RegistrationResponse registration(RegistrationRequest registrationRequest) {

        final String username = registrationRequest.getUsername();

        final boolean existsByUsername = authRepository.existsByUsername(username);

        if (existsByUsername) {

            log.warn("{} is already being used!", username);

            final String existsUsername = exceptionMessageAccessor.getMessage(null, USERNAME_ALREADY_EXISTS);
            throw new RegistrationException(existsUsername);
        }

        final User user = UserMapper.INSTANCE.convertToUser(registrationRequest);
        user.setUserId(UUID.randomUUID());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(user.getCreatedAt());

        authRepository.save(user);

        final String registrationSuccessMessage = generalMessageAccessor.getMessage(null, REGISTRATION_SUCCESSFUL, username);

        log.info("{} registered successfully!", username);

        return new RegistrationResponse(registrationSuccessMessage);
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        User user = this.findByUsername(username);
        if (user != null) {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            user.setUpdatedAt(new Date());
            authRepository.save(user);
        }
    }

    @Override
    public RefreshTokenResponse getRefreshTokenResponse(RefreshTokenRequest refreshTokenRequest) {

        final String refreshToken = refreshTokenRequest.getRefreshToken();

        // Check if the refresh token is blacklisted
        if (redisService.isRefreshTokenBlacklisted(refreshToken)) {
            final String blacklistedTokenMessage = exceptionMessageAccessor.getMessage(null, "blacklisted_refresh_token");
            throw new InvalidTokenException(blacklistedTokenMessage);
        }

        final boolean validToken = jwtTokenService.validateToken(refreshToken);

        final String username = jwtTokenService.getUsernameFromToken(refreshToken);

        final User user = authRepository.findByUsername(username);

        if (validToken) {
            return new RefreshTokenResponse(jwtTokenService.generateRefreshToken(user));
        } else {
            final String invalidToken = exceptionMessageAccessor.getMessage(null, "invalid_token");
            throw new InvalidTokenException(invalidToken);
        }
    }


    public LoginResponse getLoginResponse(LoginRequest loginRequest) {

        final String username = loginRequest.getUsername();
        final String password = loginRequest.getPassword();

        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        final User user = authRepository.findByUsername(username);

        final String accessToken = jwtTokenService.generateAccessToken(user);
        final String refreshToken = jwtTokenService.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public LogoutResponse getLogoutResponse(LogoutRequest logoutRequest) {

        redisService.storeAccessToken(logoutRequest.getAccessToken());
        redisService.storeRefreshToken(logoutRequest.getRefreshToken());

        final String logoutSuccessMessage = generalMessageAccessor.getMessage(null, "logout_successful");
        return new LogoutResponse(logoutSuccessMessage);
    }

}