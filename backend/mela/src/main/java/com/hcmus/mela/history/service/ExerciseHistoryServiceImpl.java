package com.hcmus.mela.history.service;

import com.hcmus.mela.shared.utils.ProjectConstants;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.history.dto.dto.ExerciseHistoryDto;
import com.hcmus.mela.history.dto.request.ExerciseResultRequest;
import com.hcmus.mela.history.dto.response.ExerciseResultResponse;
import com.hcmus.mela.history.mapper.ExerciseAnswerMapper;
import com.hcmus.mela.history.mapper.ExerciseHistoryMapper;
import com.hcmus.mela.history.model.BestResultByExercise;
import com.hcmus.mela.history.model.ExerciseAnswer;
import com.hcmus.mela.history.model.ExerciseHistory;
import com.hcmus.mela.history.model.ExercisesCountByLecture;
import com.hcmus.mela.history.repository.ExerciseHistoryRepository;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ExerciseHistoryServiceImpl implements ExerciseHistoryService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;

    private final LectureService lectureService;

    private final ExerciseInfoService exerciseInfoService;

    @Override
    public ExerciseResultResponse getExerciseResultResponse(UUID userId, ExerciseResultRequest exerciseResultRequest) {
        saveExercise(exerciseResultRequest, userId);
        log.info("Exercise result saved successfully for user: {}", userId);

        return new ExerciseResultResponse("Exercise result saved successfully for user: " + userId);
    }

    private void saveExercise(ExerciseResultRequest exerciseResultRequest, UUID userId) {
        ExerciseDto exerciseInfo = exerciseInfoService.findByExerciseId(exerciseResultRequest.getExerciseId());
        LectureDto lectureInfo = lectureService.getLectureById(exerciseInfo.getLectureId());

        List<ExerciseAnswer> answers = exerciseResultRequest.getAnswers().stream()
                .map(ExerciseAnswerMapper.INSTANCE::convertToExerciseAnswer)
                .toList();

        Double score = answers.stream()
                .filter(ExerciseAnswer::getIsCorrect)
                .count() * 1.0 / answers.size() * 100;

        ExerciseHistory exerciseHistory = ExerciseHistory.builder()
                .id(UUID.randomUUID())
                .lectureId(lectureInfo.getLectureId())
                .userId(userId)
                .exerciseId(exerciseInfo.getExerciseId())
                .levelId(lectureInfo.getLevelId())
                .topicId(lectureInfo.getTopicId())
                .score(score)
                .startedAt(exerciseResultRequest.getStartedAt())
                .completedAt(exerciseResultRequest.getCompletedAt())
                .answers(answers)
                .build();

        exerciseHistoryRepository.save(exerciseHistory);
    }

    @Override
    public Map<UUID, Integer> getPassedExerciseCountOfUser(UUID userId) {
        List<ExercisesCountByLecture> exercisesCountByLectureList = exerciseHistoryRepository
                .countTotalPassExerciseOfUser(userId, ProjectConstants.EXERCISE_PASS_SCORE);

        if (exercisesCountByLectureList == null || exercisesCountByLectureList.isEmpty()) {
            return Collections.emptyMap();
        }

        return exercisesCountByLectureList.stream()
                .collect(Collectors.toMap(
                        ExercisesCountByLecture::getLectureId,
                        ExercisesCountByLecture::getTotalExercises
                ));
    }

    @Override
    public Map<UUID, Double> getExerciseBestScoresOfUserByLecture(UUID userId, UUID lectureId) {
        List<BestResultByExercise> bestResultByExerciseList = exerciseHistoryRepository.getBestExerciseResultsOfUserByLectureId(userId, lectureId);
        Map<UUID, Double> bestScoreMap = new HashMap<>();
        for (BestResultByExercise bestResultByExercise : bestResultByExerciseList) {
            bestScoreMap.put(bestResultByExercise.getExerciseId(), bestResultByExercise.getScore());
        }
        return bestScoreMap;
    }

    @Override
    public List<ExerciseHistoryDto> getExerciseHistoryByUserAndLevel(UUID userId, UUID levelId) {
        List<ExerciseHistory> exerciseHistories = exerciseHistoryRepository.findAllByUserIdAndLevelId(userId, levelId);
        if (exerciseHistories == null || exerciseHistories.isEmpty()) {
            return new ArrayList<>();
        }
        return exerciseHistories.stream().map(ExerciseHistoryMapper.INSTANCE::converToExerciseHistoryDto).toList();
    }
}
