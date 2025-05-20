package com.hcmus.mela.review.repository;

import com.hcmus.mela.review.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends MongoRepository<Review, UUID>, ReviewCustomRepository {
    @Query("{ 'user_id': ?0, 'created_at': { $gte: ?1, $lt: ?2 } }")
    List<Review> findAllByUserIdAndCreatedAtBetween(UUID userId, Date startOfDay, Date endOfDay);

    Review findByReviewId(UUID reviewId);
}
