package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Lecture findById(UUID lectureId) {
        Query query = new Query(Criteria.where("_id").is(lectureId));
        return mongoTemplate.findOne(query, Lecture.class, "lectures");
    }

    @Override
    public List<Lecture> findLecturesByTopic(UUID topicId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("topic_id").is(topicId)),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description")
                        .and("exercises").size().as("total_exercises")
        );
        AggregationResults<Lecture> result = mongoTemplate.aggregate(
                aggregation,
                "lectures",
                Lecture.class
        );
        return result.getMappedResults();
    }

    @Override
    public List<Lecture> findLecturesByTopicAndLevel(UUID topicId, UUID levelId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("topic_id").is(topicId)),
                Aggregation.match(Criteria.where("level_id").is(levelId)),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description")
                        .and("exercises").size().as("total_exercises")
        );
        AggregationResults<Lecture> result = mongoTemplate.aggregate(
                aggregation,
                "lectures",
                Lecture.class
        );
        return result.getMappedResults();
    }


    @Override
    public List<Lecture> findLecturesByKeyword(String keyword) {
        Aggregation aggregation = Aggregation.newAggregation(
                (keyword != null && !keyword.isEmpty())
                        ? Aggregation.match(Criteria.where("name").regex(keyword, "i"))
                        : Aggregation.match(new Criteria()),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description")
                        .and("exercises").size().as("total_exercises")
        );
        AggregationResults<Lecture> result = mongoTemplate.aggregate(
                aggregation,
                "lectures",
                Lecture.class
        );
        return result.getMappedResults();
    }

    @Override
    public List<Lecture> findLecturesByRecent(UUID userId, Integer size) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("user_id").is(userId)),
                Aggregation.sort(Sort.by(Sort.Order.desc("end_at"))),
                Aggregation.group("lecture_id")
                        .first("end_at").as("end_at"),
                Aggregation.sort(Sort.by(Sort.Order.desc("end_at"))),
                Aggregation.limit(size),
                Aggregation.lookup("lectures", "_id", "_id", "lecture"),
                Aggregation.unwind("lecture"),
                Aggregation.project()
                        .and("_id").as("_id")
                        .and("lecture.level_id").as("level_id")
                        .and("lecture.topic_id").as("topic_id")
                        .and("lecture.name").as("name")
                        .and("lecture.description").as("description"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.project("_id", "level_id", "topic_id", "name", "description")
                        .and("exercises").size().as("total_exercises")
        );
        AggregationResults<Lecture> result = mongoTemplate.aggregate(
                aggregation,
                "exercise_results",
                Lecture.class
        );
        return result.getMappedResults();
    }
}
