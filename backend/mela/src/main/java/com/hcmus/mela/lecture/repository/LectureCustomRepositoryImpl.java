package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.model.LectureActivity;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LectureCustomRepositoryImpl implements LectureCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Lecture> findLecturesByTopicAndLevel(UUID topicId, UUID levelId) {
        Criteria criteria = Criteria.where("topic_id").is(topicId)
                .and("level_id").is(levelId);

        Aggregation aggregation = buildLectureAggregation(criteria);
        return executeLectureAggregation(aggregation);
    }

    @Override
    public List<Lecture> findLecturesByKeyword(String keyword) {
        Criteria criteria = (keyword != null && !keyword.trim().isEmpty())
                ? Criteria.where("name").regex(keyword, "i")
                : new Criteria(); // match all

        Aggregation aggregation = buildLectureAggregation(criteria);
        return executeLectureAggregation(aggregation);
    }

    @Override
    public List<LectureActivity> findRecentLectureByUserExerciseHistory(UUID userId, Integer size) {
        List<AggregationOperation> pipeline = new ArrayList<>();
        pipeline.add(Aggregation.match(Criteria.where("user_id").is(userId)));

        pipeline.addAll(buildLectureJoinAggregation(size));

        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        AggregationResults<LectureActivity> results = mongoTemplate.aggregate(
                aggregation,
                "exercise_histories",
                LectureActivity.class);
        return results.getMappedResults();
    }

    @Override
    public List<LectureActivity> findRecentLectureByUserSectionHistory(UUID userId, Integer size) {
        List<AggregationOperation> pipeline = new ArrayList<>();
        pipeline.add(Aggregation.match(Criteria.where("user_id").is(userId)));
        pipeline.add(Aggregation.project("lecture_id", "completed_sections"));
        pipeline.add(Aggregation.unwind("completed_sections"));
        pipeline.add(Aggregation.project("lecture_id")
                .and("completed_sections.completed_at").as("completed_at"));

        pipeline.addAll(buildLectureJoinAggregation(size));

        Aggregation aggregation = Aggregation.newAggregation(pipeline);
        AggregationResults<LectureActivity> results = mongoTemplate.aggregate(
                aggregation,
                "lecture_histories",
                LectureActivity.class);
        return results.getMappedResults();
    }

    @Override
    public List<Lecture> findCompleteLecturesWithWrongExercises(UUID userId) {
        // Match by userId and completed_at ≥ 3 days ago
        MatchOperation userAndDateMatch = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("user_id").is(userId),
                        Criteria.where("completed_at").lte(LocalDateTime.now().minusDays(3))
                )
        );

        AggregationOperation lookupExercise = context -> new Document("$lookup",
                new Document("from", "exercise_histories")
                        .append("let", new Document("lectureId", "$lecture_id").append("userId", "$user_id"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match",
                                        new Document("$expr",
                                                new Document("$and", Arrays.asList(
                                                        new Document("$eq", Arrays.asList("$lecture_id", "$$lectureId")),
                                                        new Document("$eq", Arrays.asList("$user_id", "$$userId"))
                                                ))
                                        )
                                )
                        ))
                        .append("as", "exerciseResults")
        );


        // Unwind exerciseResults
        UnwindOperation unwindExercises = Aggregation.unwind("exerciseResults", true);

        ProjectionOperation projectRenameExerciseFields = Aggregation.project()
                .andInclude("user_id", "lecture_id", "completed_at", "otherRootFieldsIfAny...") // include root fields you need
                .and("exerciseResults.progress").as("exerciseResults.progress")
                .and("exerciseResults.score").as("exerciseResults.score")
                .and("exerciseResults.completed_at").as("exerciseCompletedAt");

        AddFieldsOperation addDefaultScore = Aggregation.addFields()
                .addFieldWithValue("exerciseScore",
                        ConditionalOperators.ifNull("exerciseResults.score").then(0))
                .build();

        MatchOperation resultMatch = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("progress").is(100),
                        Criteria.where("exerciseScore").lt(100)
                )
        );


        // Lookup lectures by lecture_id
        LookupOperation lookupLecture = LookupOperation.newLookup()
                .from("lectures")
                .localField("lecture_id")
                .foreignField("_id")
                .as("lecture");

        // Unwind lectures (should be only one)
        UnwindOperation unwindLecture = Aggregation.unwind("lecture");

        // Replace root with lecture document
        GroupOperation groupAndGetMaxScore = Aggregation.group("lecture_id")
                .first("lecture").as("lecture")
                .first("completed_at").as("completedAt")
                .max("exerciseResults.score").as("maxScore");

        SortOperation sortOperation = Aggregation.sort(
                Sort.by(Sort.Order.asc("completedAt"), Sort.Order.asc("maxScore"))
        );

        ReplaceRootOperation replaceRootFinal = Aggregation.replaceRoot("lecture");

        Aggregation aggregation = Aggregation.newAggregation(
                userAndDateMatch,
                lookupExercise,
                unwindExercises,
                addDefaultScore,
                resultMatch,
                lookupLecture,
                unwindLecture,
                groupAndGetMaxScore,
                sortOperation,
                replaceRootFinal
        );


        AggregationResults<Lecture> results = mongoTemplate.aggregate(
                aggregation, "lecture_histories", Lecture.class
        );

        return results.getMappedResults();
    }

    private List<AggregationOperation> buildLectureJoinAggregation(Integer size) {
        return Arrays.asList(
                Aggregation.sort(Sort.Direction.DESC, "completed_at"),
                Aggregation.group("lecture_id")
                        .first("completed_at").as("completed_at"),
                Aggregation.sort(Sort.Direction.DESC, "completed_at"),
                Aggregation.limit(size),
                Aggregation.lookup("lectures", "_id", "_id", "lecture"),
                Aggregation.unwind("lecture"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.project()
                        .and("lecture._id").as("lecture_id")
                        .and("lecture.level_id").as("level_id")
                        .and("lecture.topic_id").as("topic_id")
                        .and("lecture.ordinal_number").as("ordinal_number")
                        .and("lecture.name").as("name")
                        .and("lecture.description").as("description")
                        .and("completed_at").as("completed_at")
                        .and("exercises").size().as("total_exercises")
        );
    }

    private Aggregation buildLectureAggregation(Criteria matchCriteria) {
        return Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description"),
                Aggregation.lookup("exercises", "_id", "lecture_id", "exercises"),
                Aggregation.project("_id", "level_id", "topic_id", "ordinal_number", "name", "description")
                        .and("exercises").size().as("total_exercises")
        );
    }

    private List<Lecture> executeLectureAggregation(Aggregation aggregation) {
        AggregationResults<Lecture> results = mongoTemplate.aggregate(aggregation, "lectures", Lecture.class);
        return results.getMappedResults();
    }
}