package com.hcmus.mela.review.repository;

import com.hcmus.mela.review.dto.request.UpdateReviewRequest;
import com.hcmus.mela.review.model.Review;

public interface ReviewCustomRepository {

    Review updateReview(Review review);
}
