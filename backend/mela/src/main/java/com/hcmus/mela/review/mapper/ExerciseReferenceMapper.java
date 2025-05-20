package com.hcmus.mela.review.mapper;

import com.hcmus.mela.review.dto.ExerciseReferenceDto;
import com.hcmus.mela.review.model.ExerciseReference;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExerciseReferenceMapper {
    ExerciseReferenceMapper INSTANCE = Mappers.getMapper(ExerciseReferenceMapper.class);

    ExerciseReferenceDto exerciseReferenceToExerciseReferenceDto(ExerciseReference exerciseReference);

    ExerciseReference exerciseReferenceDtoToExerciseReference(ExerciseReferenceDto exerciseReferenceDto);
}
