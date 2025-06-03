package com.hcmus.mela.token.service;

import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.token.dto.response.GetUserTokenResponse;
import com.hcmus.mela.token.model.Token;
import com.hcmus.mela.token.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;


    private final GeneralMessageAccessor generalMessageAccessor;

    private final String TOKEN_FOUND = "token_found_successful";

    private final String REDUCE_TOKEN_SUCCESS = "reduce_token_successful";

    @Override
    public GetUserTokenResponse getUserToken(UUID userId) {

        Token token = tokenRepository.findByUserId(userId);

        if (token == null) {
            token = new Token(userId, 5);

            tokenRepository.save(token);
        }

        String tokenFoundMessage = generalMessageAccessor.getMessage(null, TOKEN_FOUND, userId);

        return new GetUserTokenResponse(tokenFoundMessage, token.getToken());

    }

    @Override
    public int reduceUserToken(UUID userId) {
        Token token = tokenRepository.findByUserId(userId);

        if (token == null) {
            token = new Token(userId, 5);

            tokenRepository.save(token);
        }

        token.setToken(token.getToken() - 1);

        tokenRepository.save(token);

        return token.getToken();
    }

    @Override
    public boolean validateUserToken(UUID userId) {
        Token token = tokenRepository.findByUserId(userId);

        if (token == null) {
            token = new Token(userId, 5);
            tokenRepository.save(token);
        }

        if (token.getToken() <= 0) {
            return false;
        }

        return true;
    }
}
