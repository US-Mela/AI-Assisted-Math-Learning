package com.hcmus.mela.review.mapper;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.model.Exercise;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.service.LectureService;
import com.hcmus.mela.review.dto.ReviewDto;
import com.hcmus.mela.review.model.ExerciseReference;
import com.hcmus.mela.review.model.Review;
import com.hcmus.mela.review.model.ReviewType;
import com.hcmus.mela.review.model.SectionReference;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
public class ReviewMapperImpl implements ReviewMapper {

    final private ExerciseInfoService exerciseInfoService;

    final private LectureService lectureService;

    @Override
    public ReviewDto reviewToReviewDto(Review review) {

        List<ExerciseDto> exerciseList = new ArrayList<>();

        List<SectionDto> sectionList = new ArrayList<>();

        if (review.getReviewType() == ReviewType.EXERCISE) {
            for (ExerciseReference exerciseRef : review.getExerciseList()) {
                exerciseList.add(exerciseInfoService.findByExerciseId(exerciseRef.getExerciseId()));
            }
        }

        if (review.getReviewType() == ReviewType.SECTION) {
            for (SectionReference sectionRef : review.getSectionList()) {
                sectionList.add(lectureService.getLectureById(sectionRef.getLectureId())
                        .getSections()
                        .get(sectionRef.getOrdinalNumber() - 1));
            }
        }

        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .createdAt(review.getCreatedAt())
                .reviewType(review.getReviewType())
                .exerciseList(exerciseList)
                .sectionList(sectionList)
                .build();
    }
}
