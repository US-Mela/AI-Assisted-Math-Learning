package com.hcmus.mela.token.service;

import com.hcmus.mela.token.dto.response.GetUserTokenResponse;

import java.util.UUID;

public interface TokenService {
    GetUserTokenResponse getUserToken(UUID userId);

    int reduceUserToken(UUID userId);

    boolean validateUserToken(UUID userId);
}
