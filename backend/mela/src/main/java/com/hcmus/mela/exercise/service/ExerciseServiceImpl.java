package com.hcmus.mela.exercise.service;

import com.hcmus.mela.common.async.AsyncCustomService;
import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.common.utils.ProjectConstants;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseResultDto;
import com.hcmus.mela.exercise.dto.dto.ExerciseStatDetailDto;
import com.hcmus.mela.exercise.dto.dto.QuestionDto;
import com.hcmus.mela.exercise.dto.request.ExerciseRequest;
import com.hcmus.mela.exercise.dto.response.ExerciseResponse;
import com.hcmus.mela.exercise.dto.response.QuestionResponse;
import com.hcmus.mela.exercise.mapper.ExerciseMapper;
import com.hcmus.mela.exercise.mapper.ExerciseStatDetailMapper;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.model.ExerciseStatus;
import com.hcmus.mela.exercise.repository.ExerciseRepository;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private static final String EXERCISE_FOUND = "exercise_found_successful";

    private static final String EXERCISES_FOUND = "exercises_of_lecture_found_successful";

    private final GeneralMessageAccessor generalMessageAccessor;

    private final ExerciseRepository exerciseRepository;

    private final ExerciseValidationService exerciseValidationService;

    private final LectureService lectureService;

    private final ExerciseInfoService exerciseInfoService;

    private final ExerciseHistoryService exerciseHistoryService;

    private final AsyncCustomService asyncService;

    @Override
    public QuestionResponse getExercise(ExerciseRequest exerciseRequest) {
        exerciseValidationService.validateExercise(exerciseRequest);

        final UUID exerciseId = exerciseRequest.getExerciseId();

        ExerciseDto exerciseDto = exerciseInfoService.findByExerciseId(exerciseId);
        List<QuestionDto> questionDtoList = new ArrayList<>();
        if (exerciseDto != null) {
            questionDtoList = exerciseDto.getQuestions();
        }

        final String exerciseSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISE_FOUND, exerciseId);
        log.info(exerciseSuccessMessage);

        return new QuestionResponse(exerciseSuccessMessage, questionDtoList.size(), questionDtoList);
    }

    @Override
    public ExerciseResponse getAllExercisesInLecture(ExerciseRequest exerciseRequest) {
        exerciseValidationService.validateLecture(exerciseRequest);

        UUID lectureId = exerciseRequest.getLectureId();
        CompletableFuture<LectureDto> lectureFuture = asyncService.runAsync(
                () -> lectureService.getLectureById(lectureId),
                null);
        CompletableFuture<List<Exercise>> exerciseFuture = asyncService.runAsync(
                () -> exerciseRepository.findAllByLectureId(lectureId),
                Collections.emptyList());

        CompletableFuture.allOf(lectureFuture, exerciseFuture).join();

        LectureDto lecture = lectureFuture.join();
        List<Exercise> exercises = exerciseFuture.join();

        if (exercises == null || exercises.isEmpty() || lecture == null) {
            final String exercisesNotFoundMessage = generalMessageAccessor.getMessage(null, "exercises_not_found", lectureId);
            log.info(exercisesNotFoundMessage);
            return new ExerciseResponse(exercisesNotFoundMessage, 0, new ArrayList<>());
        }

        List<ExerciseStatDetailDto> exerciseStatDetailDtoList = mapExercisesToStatDetails(
                exercises,
                exerciseRequest.getUserId(),
                lectureId,
                lecture.getTopicId(),
                lecture.getLevelId()
        );

        final String exercisesSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISES_FOUND, lectureId);
        log.info(exercisesSuccessMessage);

        return new ExerciseResponse(
                exercisesSuccessMessage,
                exerciseStatDetailDtoList.size(),
                exerciseStatDetailDtoList);
    }

    @Override
    public List<ExerciseDto> getListOfExercisesInLecture(UUID lectureId) {
        List<Exercise> exercises = exerciseRepository.findAllByLectureId(lectureId);

        return exercises.stream()
                .map(ExerciseMapper.INSTANCE::converToExerciseDto)
                .toList();
    }

    private List<ExerciseStatDetailDto> mapExercisesToStatDetails(
            List<Exercise> exercises,
            UUID userId,
            UUID lectureId,
            UUID topicId,
            UUID levelId
    ) {
        Map<UUID, Double> exerciseBestScoreMap = exerciseHistoryService.getExerciseBestScoresOfUserByLecture(userId, lectureId);

        List<ExerciseStatDetailDto> exerciseStatDetailDtoList = new ArrayList<>();
        for (Exercise exercise : exercises) {
            final UUID exerciseId = exercise.getExerciseId();
            final Integer numberOfQuestions = Optional.ofNullable(exercise.getQuestions()).map(List::size).orElse(0);

            ExerciseStatDetailDto exerciseStatDetailDto = ExerciseStatDetailMapper.INSTANCE.convertToExerciseStatDetailDto(exercise);
            exerciseStatDetailDto.setTopicId(topicId);
            exerciseStatDetailDto.setLevelId(levelId);
            exerciseStatDetailDto.setTotalQuestions(numberOfQuestions);

            if (exerciseBestScoreMap.containsKey(exerciseId)) {
                double bestScore = exerciseBestScoreMap.get(exerciseId);
                ExerciseResultDto exerciseResultDto = ExerciseResultDto.builder()
                        .status(bestScore >= ProjectConstants.EXERCISE_PASS_SCORE ? ExerciseStatus.PASS : ExerciseStatus.IN_PROGRESS)
                        .totalAnswers(numberOfQuestions)
                        .totalCorrectAnswers((int) Math.round(numberOfQuestions * bestScore / 100))
                        .build();
                exerciseStatDetailDto.setBestResult(exerciseResultDto);
            } else {
                exerciseStatDetailDto.setBestResult(null);
            }

            exerciseStatDetailDtoList.add(exerciseStatDetailDto);
        }
        return exerciseStatDetailDtoList;
    }

    @Override
    public Exercise findByQuestionId(UUID questionId) {
        return exerciseRepository.findByQuestionsQuestionId(questionId);
    }

    @Override
    public void updateQuestionHint(Exercise exercise) {
        Exercise result = exerciseRepository.updateQuestionHint(exercise);
        if (result == null) {
            log.warn("Exercise {} not found", exercise.getExerciseId());
            return;
        }
        log.debug("Exercise {} updated successfully", result.getExerciseId());
    }
}
