package com.hcmus.mela.service;

import com.hcmus.mela.dto.request.ExerciseRequest;
import com.hcmus.mela.dto.response.ExerciseResponse;
import com.hcmus.mela.dto.service.ExerciseDto;
import com.hcmus.mela.dto.service.UserExerciseDto;
import com.hcmus.mela.model.mongo.Exercise;
import com.hcmus.mela.repository.mongo.ExerciseRepository;
import com.hcmus.mela.security.mapper.ExerciseMapper;
import com.hcmus.mela.utils.GeneralMessageAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private static final String EXERCISE_FOUND = "exercise_found_successful";
    private static final String EXERCISES_FOUND = "exercises_of_lecture_found_successful";
    private final ExerciseRepository exerciseRepository;
    private final ExerciseValidationService exerciseValidationService;
    private final GeneralMessageAccessor generalMessageAccessor;
    private final UserExerciseService userExerciseService;
    private final QuestionService questionService;

    @Override
    public Exercise findByExerciseId(Integer exerciseId) {

        return exerciseRepository.findByExerciseId(exerciseId);
    }

    @Override
    public List<Exercise> findAllExercisesInLecture(Integer lectureId) {
        return exerciseRepository.findAllByLectureId(lectureId);
    }

    @Override
    public ExerciseResponse getExercise(ExerciseRequest exerciseRequest) {
        exerciseValidationService.validateExercise(exerciseRequest);

        final Integer exerciseId = exerciseRequest.getExerciseId();
        final Integer userId = exerciseRequest.getUserId();
        final Integer numberOfQuestions = questionService.getNumberOfQuestionsInExercise(exerciseId);

        Exercise exercise = findByExerciseId(exerciseId);

        ExerciseDto exerciseDto = ExerciseMapper.INSTANCE.convertToExerciseDto(exercise);
        UserExerciseDto userExerciseDto = userExerciseService.getUserExercise(userId, exerciseId);

        if (userExerciseDto != null) {
            userExerciseDto.setTotalAnswer(numberOfQuestions);
        }

        exerciseDto.setUserExercise(userExerciseDto);
        
        final String exerciseSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISE_FOUND, exerciseId);

        log.info(exerciseSuccessMessage);

        return new ExerciseResponse(exerciseSuccessMessage,List.of(exerciseDto));
    }

    @Override
    public ExerciseResponse getAllExercisesInLecture(ExerciseRequest exerciseRequest) {
        exerciseValidationService.validateLecture(exerciseRequest);

        final Integer lectureId = exerciseRequest.getLectureId();

        List<Exercise> exercises = findAllExercisesInLecture(lectureId);

        List<ExerciseDto> exerciseDtos = new ArrayList<>();

        for(Exercise exercise: exercises) {
            final Integer exerciseId = exercise.getExerciseId();
            final Integer userId = exerciseRequest.getUserId();
            final Integer numberOfQuestions = questionService.getNumberOfQuestionsInExercise(exerciseId);

            ExerciseDto exerciseDto = ExerciseMapper.INSTANCE.convertToExerciseDto(exercise);

            UserExerciseDto userExerciseDto = userExerciseService.getUserExercise(userId, exerciseId);

            if (userExerciseDto != null) {
                userExerciseDto.setTotalAnswer(numberOfQuestions);
            }

            exerciseDto.setUserExercise(userExerciseDto);

            exerciseDtos.add(exerciseDto);
        }

        final String exercisesSuccessMessage = generalMessageAccessor.getMessage(null, EXERCISES_FOUND, lectureId);

        log.info(exercisesSuccessMessage);

        return new ExerciseResponse(exercisesSuccessMessage,exerciseDtos);
    }
}
