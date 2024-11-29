package com.hcmus.mela.exercise.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private UUID exerciseId;

    private UUID lectureId;

    private String exerciseName;

    private Integer ordinalNumber;

    private Integer totalQuestions;

    private ExerciseResultDto bestResult;
}