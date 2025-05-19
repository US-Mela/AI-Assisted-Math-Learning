package com.hcmus.mela.review.mapper;

import com.hcmus.mela.review.dto.ReviewDto;
import com.hcmus.mela.review.model.Review;
import org.mapstruct.Mapper;

@Mapper
public interface ReviewMapper {
    ReviewDto reviewToReviewDto(Review review);
}
