package com.hcmus.mela.review.service;

import com.hcmus.mela.review.dto.request.UpdateReviewRequest;
import com.hcmus.mela.review.dto.response.GetReviewsResponse;
import com.hcmus.mela.review.dto.response.UpdateReviewResponse;

import java.util.UUID;

public interface ReviewService {
    GetReviewsResponse getReviews(UUID userId);

    UpdateReviewResponse updateReview(UUID reviewId, UpdateReviewRequest updateReviewRequest);
}
