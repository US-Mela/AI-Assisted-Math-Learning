package com.hcmus.mela.lecture.repository;

import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.model.LectureActivity;

import java.util.List;
import java.util.UUID;

public interface LectureCustomRepository {

    List<Lecture> findLecturesByTopicAndLevel(UUID topicId, UUID levelId);

    List<Lecture> findLecturesByKeyword(String keyword);

    List<LectureActivity> findRecentLectureByUserExerciseHistory(UUID userId, Integer size);

    List<LectureActivity> findRecentLectureByUserSectionHistory(UUID userId, Integer size);

    List<Lecture> findCompleteLecturesWithWrongExercises(UUID userId);
}
