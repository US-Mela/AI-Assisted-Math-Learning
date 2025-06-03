package com.hcmus.mela.token.scheduler;

import com.hcmus.mela.token.model.Token;
import com.hcmus.mela.token.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TokenResetScheduler {
    private TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    public void resetTokens() {
        List<Token> usersWithLowToken = tokenRepository.findByTokenLessThan(5);

        for (Token user : usersWithLowToken) {
            user.setToken(5);
        }

        tokenRepository.saveAll(usersWithLowToken);
    }
}
