package com.hcmus.mela.streak.service;

import com.hcmus.mela.shared.utils.ExceptionMessageAccessor;
import com.hcmus.mela.shared.utils.GeneralMessageAccessor;
import com.hcmus.mela.streak.dto.response.GetStreakResponse;
import com.hcmus.mela.streak.dto.response.UpdateStreakResponse;
import com.hcmus.mela.streak.model.Streak;
import com.hcmus.mela.streak.repository.StreakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakServiceImpl implements StreakService {

    private static final String STREAK_FOUND = "streak_found_successful";

    private static final String UPDATE_STREAK_SUCCESS = "update_streak_successful";

    private static final String USER_NOT_FOUND = "user_not_found";

    private static final String STREAK_ALREADY_UPDATED = "streak_already_updated";

    private final StreakRepository streakRepository;

    private final GeneralMessageAccessor generalMessageAccessor;

    private final ExceptionMessageAccessor exceptionMessageAccessor;

    @Override
    public GetStreakResponse getStreak(UUID userId) {

        Streak streak = streakRepository.findByUserId(userId);

        if (streak == null) {
            streak = new Streak(userId, 0, new Date(), new Date(), 0);
            streakRepository.save(streak);

            final String getStreakSuccessMessage = generalMessageAccessor.getMessage(null, STREAK_FOUND, userId);

            log.info(getStreakSuccessMessage);

            return new GetStreakResponse(streak.getStreakDays(),
                    streak.getUpdatedAt(),
                    streak.getLongestStreak(),
                    getStreakSuccessMessage);
        }

        if (ChronoUnit.DAYS.between(
                streak.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) > 1) {

            streak.setStreakDays(0);
            streakRepository.updateStreak(streak);
        }

        final String getStreakSuccessMessage = generalMessageAccessor.getMessage(null, STREAK_FOUND, userId);

        log.info(getStreakSuccessMessage);

        return new GetStreakResponse(streak.getStreakDays(),
                streak.getUpdatedAt(),
                streak.getLongestStreak(),
                getStreakSuccessMessage);
    }

    @Override
    public UpdateStreakResponse updateStreak(UUID userId) {
        Streak streak = streakRepository.findByUserId(userId);

        if (streak == null) {
            streak = new Streak(userId, 1, new Date(), new Date(), 1);
            streakRepository.save(streak);

            final String updateStreakSuccessMessage = generalMessageAccessor.getMessage(null, UPDATE_STREAK_SUCCESS, userId);

            log.info(updateStreakSuccessMessage);

            return new UpdateStreakResponse(updateStreakSuccessMessage);
        }

        if (ChronoUnit.DAYS.between(
                streak.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) == 0
                && streak.getStreakDays() != 0) {
            final String streakAlreadyUpdated = exceptionMessageAccessor.getMessage(null, STREAK_ALREADY_UPDATED, userId);

            return new UpdateStreakResponse(streakAlreadyUpdated);
        }

        streak.setStreakDays(streak.getStreakDays() + 1);

        streak.setUpdatedAt(new Date());

        if (streak.getStreakDays() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getStreakDays());
        }

        streakRepository.updateStreak(streak);

        final String updateStreakSuccessMessage = generalMessageAccessor.getMessage(null, UPDATE_STREAK_SUCCESS, userId);

        log.info(updateStreakSuccessMessage);

        return new UpdateStreakResponse(updateStreakSuccessMessage);
    }
}
