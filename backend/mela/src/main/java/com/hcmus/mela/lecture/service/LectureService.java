package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.response.GetLectureSectionsResponse;

import java.util.UUID;

public interface LectureService {

    LectureDto getLectureById(UUID lectureId);

    Integer getLectureOrdinalNumber(UUID lectureId);

    GetLectureSectionsResponse getLectureSections(UUID lectureId);

    LectureDto getLectureByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber);
}