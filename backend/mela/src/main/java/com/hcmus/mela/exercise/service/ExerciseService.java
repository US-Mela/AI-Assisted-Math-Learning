package com.hcmus.mela.exercise.service;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.dto.request.ExerciseRequest;
import com.hcmus.mela.exercise.dto.response.ExerciseResponse;
import com.hcmus.mela.exercise.dto.response.QuestionResponse;
import com.hcmus.mela.exercise.model.Exercise;

import java.util.List;
import java.util.UUID;

public interface ExerciseService {

    QuestionResponse getExercise(ExerciseRequest exerciseRequest);

    ExerciseResponse getAllExercisesInLecture(ExerciseRequest exerciseRequest);

    List<ExerciseDto> getListOfExercisesInLecture(UUID lectureId);

    Exercise findByQuestionId(UUID questionId);

    void updateQuestionHint(Exercise exercise);
}
