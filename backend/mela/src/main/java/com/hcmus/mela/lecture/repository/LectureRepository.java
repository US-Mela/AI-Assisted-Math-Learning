package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LectureRepository extends MongoRepository<Lecture, UUID>, LectureCustomRepository {

    Lecture findByLectureId(UUID lectureId);

    @Query("{ 'lectureId': { '$in': ?0 } }")
    List<Lecture> findAllByLectureIdList(List<UUID> lectureIdList);

    Lecture findByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);
}
