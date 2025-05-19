package com.hcmus.mela.review.dto;

import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.review.model.ReviewType;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private UUID reviewId;

    private UUID userId;

    private ReviewType reviewType;

    private Date createdAt;

    private List<ExerciseDto> exerciseList;

    private List<SectionDto> sectionList;
}
