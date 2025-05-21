package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.LectureHistoryDto;
import com.hcmus.mela.history.dto.request.SaveLectureSectionRequest;
import com.hcmus.mela.history.dto.response.SaveLectureSectionResponse;
import com.hcmus.mela.history.model.LectureHistory;

import java.util.List;
import java.util.UUID;

public interface LectureHistoryService {

    SaveLectureSectionResponse saveSection(UUID userId, SaveLectureSectionRequest saveLectureSectionRequest);

    List<LectureHistoryDto> getLectureHistoryByUserAndLevel(UUID userId, UUID levelId);

    List<LectureHistory> getBestProgressHistoriesGroupedByLecture(UUID userId);

}