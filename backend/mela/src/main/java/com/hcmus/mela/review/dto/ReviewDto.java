package com.hcmus.mela.review.dto;

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

    private Date createdAt;

    private List<ExerciseReferenceDto> exerciseList;

    private List<SectionReferenceDto> sectionList;
}
