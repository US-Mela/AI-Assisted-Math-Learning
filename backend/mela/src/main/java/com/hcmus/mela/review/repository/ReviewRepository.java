package com.hcmus.mela.review.repository;

import com.hcmus.mela.review.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends MongoRepository<Review, UUID> {
    List<Review> findAllByUserIdAndCreatedAtBetween(UUID userId, Date startOfDay, Date endOfDay);
}
