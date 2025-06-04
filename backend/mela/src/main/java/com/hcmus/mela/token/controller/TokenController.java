package com.hcmus.mela.token.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.token.dto.response.GetUserTokenResponse;
import com.hcmus.mela.token.dto.response.IncreaseUserTokenResponse;
import com.hcmus.mela.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tokens")
@Slf4j
public class TokenController {

    private final TokenService tokenService;

    private final JwtTokenService jwtTokenService;

    @GetMapping
    @Operation(
            tags = "Token Service",
            summary = "Get tokens by user ID",
            description = "Retrieves all tokens of the user with given user ID."
    )
    public ResponseEntity<GetUserTokenResponse> getUserToken(@RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Get tokens for user {}.", userId);

        GetUserTokenResponse response = tokenService.getUserToken(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Operation(
            tags = "Token Service",
            summary = "Increase tokens by user ID",
            description = "Increase the token amount of the user with given user ID."
    )
    public ResponseEntity<IncreaseUserTokenResponse> increaseUserToken(@RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Increase tokens for user {}.", userId);

        IncreaseUserTokenResponse response = tokenService.increaseUserToken(userId);

        return ResponseEntity.ok(response);
    }
}
