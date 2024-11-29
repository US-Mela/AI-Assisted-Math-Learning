package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.repository.UserExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserExerciseValidationService {

    private final UserExerciseRepository userExerciseRepository;

    public boolean validateUserExercise(UUID userId, Integer exerciseId) {
        return checkUserIdAndExerciseId(userId, exerciseId);
    }

    private boolean checkUserIdAndExerciseId(UUID userId, Integer exerciseId) {
        final boolean existsByUserIdAndExerciseId = userExerciseRepository.existsByUserIdAndExerciseId(userId, exerciseId);
        if (!existsByUserIdAndExerciseId) {
            log.warn("User ID {userId} hasn't studied exercise ID {exerciseId}");
        }

        return existsByUserIdAndExerciseId;
    }
}