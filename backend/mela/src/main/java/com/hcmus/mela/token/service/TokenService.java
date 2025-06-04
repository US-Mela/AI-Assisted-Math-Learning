package com.hcmus.mela.token.service;

import com.hcmus.mela.token.dto.response.GetUserTokenResponse;
import com.hcmus.mela.token.dto.response.IncreaseUserTokenResponse;

import java.util.UUID;

public interface TokenService {
    GetUserTokenResponse getUserToken(UUID userId);

    int reduceUserToken(UUID userId);

    boolean validateUserToken(UUID userId);

    IncreaseUserTokenResponse increaseUserToken(UUID userId);
}
