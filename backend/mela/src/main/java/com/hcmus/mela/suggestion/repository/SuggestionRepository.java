package com.hcmus.mela.suggestion.repository;

import com.hcmus.mela.suggestion.model.Suggestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SuggestionRepository extends MongoRepository<Suggestion, UUID>, SuggestionCustomRepository {
    @Query("{ 'user_id': ?0, 'created_at': { $gte: ?1, $lt: ?2 } }")
    List<Suggestion> findAllByUserIdAndCreatedAtBetween(UUID userId, Date start, Date end);

    Suggestion findBySuggestionId(UUID suggestionId);
}
