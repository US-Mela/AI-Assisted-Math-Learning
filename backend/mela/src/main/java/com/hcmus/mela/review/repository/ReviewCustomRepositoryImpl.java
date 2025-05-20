package com.hcmus.mela.review.repository;

import com.hcmus.mela.review.model.Review;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository{
    private final MongoTemplate mongoTemplate;

    @Override
    public Review updateReview(Review review) {
        Query query = new Query(Criteria.where("_id").is(review.getReviewId()));

        Update update = new Update().set("exercise_list", review.getExerciseList())
                .set("section_list", review.getSectionList());

        return mongoTemplate.findAndModify(query, update, Review.class);
    }
}
