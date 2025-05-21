package com.hcmus.mela.suggestion.repository;

import com.hcmus.mela.suggestion.model.Suggestion;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class SuggestionCustomRepositoryImpl implements SuggestionCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Suggestion updateSuggestion(Suggestion suggestion) {
        Query query = new Query(Criteria.where("_id").is(suggestion.getSuggestionId()));

        Update update = new Update().set("suggestion_list", suggestion.getSectionList());

        return mongoTemplate.findAndModify(query, update, Suggestion.class);
    }
}
