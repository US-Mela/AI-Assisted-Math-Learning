package com.hcmus.mela.lecture.service;

import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.response.GetLecturesByLevelResponse;
import com.hcmus.mela.lecture.dto.response.GetLecturesResponse;
import com.hcmus.mela.lecture.model.Lecture;

import java.util.List;
import java.util.UUID;

public interface LectureListService {

    GetLecturesByLevelResponse getLecturesByLevel(UUID userId, UUID levelId);

    GetLecturesResponse getLecturesByKeyword(UUID userId, String keyword);

    GetLecturesResponse getLecturesByRecent(UUID userId, Integer size);

    List<LectureDto> getLecturesNeedToBeReviewed(UUID userId);

    List<LectureDto> getLecturesNeedToBeSuggested(UUID userId);
}