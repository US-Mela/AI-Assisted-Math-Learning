package com.hcmus.mela.history.service;

import com.hcmus.mela.history.dto.dto.LectureHistoryDto;
import com.hcmus.mela.history.dto.request.SaveLectureSectionRequest;
import com.hcmus.mela.history.dto.response.SaveLectureSectionResponse;
import com.hcmus.mela.history.exception.HistoryException;
import com.hcmus.mela.history.mapper.LectureHistoryMapper;
import com.hcmus.mela.history.model.LectureCompletedSection;
import com.hcmus.mela.history.model.LectureHistory;
import com.hcmus.mela.history.repository.LectureHistoryRepository;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.service.LectureService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Slf4j
@Service
public class LectureHistoryServiceImpl implements LectureHistoryService {

    private final LectureHistoryRepository lectureHistoryRepository;

    private final LectureService lectureService;

    @Override
    public SaveLectureSectionResponse saveSection(UUID userId, SaveLectureSectionRequest saveLectureSectionRequest) {
        LectureHistory lectureHistory = lectureHistoryRepository.findByLectureIdAndUserId(saveLectureSectionRequest.getLectureId(), userId);
        LectureDto lectureInfo = lectureService.getLectureById(saveLectureSectionRequest.getLectureId());

        if (lectureInfo == null) {
            throw new HistoryException("Lecture not found for id: " + saveLectureSectionRequest.getLectureId());
        }

        boolean isUpdated = (lectureHistory != null && lectureHistory.getCompletedSections() != null);
        boolean isCompleted = false;
        if (!isUpdated) {
            lectureHistory = new LectureHistory();
            lectureHistory.setStartedAt(saveLectureSectionRequest.getCompletedAt());
        } else {
            isCompleted = lectureHistory.getProgress().equals(100);
        }

        List<LectureCompletedSection> completedSections = updateCompletedSections(lectureHistory, saveLectureSectionRequest);

        Integer progress = calculateProgress(completedSections, lectureInfo);

        if (progress.equals(100) && !isCompleted) {
            lectureHistory.setCompletedAt(saveLectureSectionRequest.getCompletedAt());
        }

        lectureHistory.setId(isUpdated ? lectureHistory.getId() : UUID.randomUUID());
        lectureHistory.setUserId(userId);
        lectureHistory.setLectureId(saveLectureSectionRequest.getLectureId());
        lectureHistory.setTopicId(lectureInfo.getTopicId());
        lectureHistory.setLevelId(lectureInfo.getLevelId());
        lectureHistory.setProgress(progress);
        lectureHistory.setCompletedSections(completedSections);

        if (isUpdated) {
            lectureHistoryRepository.updateFirstById(lectureHistory.getId(), lectureHistory);
        } else {
            lectureHistoryRepository.save(lectureHistory);
        }
        log.info("Lecture section saved successfully for user: {}, lecture: {}, progress: {}",
                userId, saveLectureSectionRequest.getLectureId(), progress);

        return new SaveLectureSectionResponse("Lecture section saved successfully for user: " + userId);
    }

    private List<LectureCompletedSection> updateCompletedSections(LectureHistory lectureHistory, SaveLectureSectionRequest saveLectureSectionRequest) {
        List<LectureCompletedSection> completedSections = Optional
                .ofNullable(lectureHistory.getCompletedSections())
                .orElse(new ArrayList<>());

        LectureCompletedSection lectureCompletedSection = completedSections
                .stream()
                .filter(section -> section.getOrdinalNumber().equals(saveLectureSectionRequest.getOrdinalNumber()))
                .findFirst().orElse(null);

        if (lectureCompletedSection == null) {
            lectureCompletedSection = new LectureCompletedSection();
            lectureCompletedSection.setOrdinalNumber(saveLectureSectionRequest.getOrdinalNumber());
            completedSections.add(lectureCompletedSection);
        }

        lectureCompletedSection.setCompletedAt(saveLectureSectionRequest.getCompletedAt());
        completedSections.sort(Comparator.comparing(LectureCompletedSection::getCompletedAt).reversed());
        return completedSections;
    }

    private Integer calculateProgress(List<LectureCompletedSection> completedSections, LectureDto lectureInfo) {
        int completedSectionsCount = completedSections.size();
        int sectionsCount = lectureInfo.getSections().size();
        return (int) (completedSectionsCount * 1.0 / sectionsCount * 100);
    }

    @Override
    public List<LectureHistoryDto> getLectureHistoryByUserAndLevel(UUID userId, UUID levelId) {
        List<LectureHistory> lectureHistories = lectureHistoryRepository.findAllByUserIdAndLevelId(userId, levelId);
        if (lectureHistories == null || lectureHistories.isEmpty()) {
            return new ArrayList<>();
        }
        return lectureHistories.stream().map(LectureHistoryMapper.INSTANCE::converToLectureHistoryDto).toList();
    }

    @Override
    public List<LectureHistory> getBestProgressHistoriesGroupedByLecture(UUID userId) {
        List<LectureHistory> lectureHistories = lectureHistoryRepository.findBestProgressHistoriesGroupedByLecture(userId);

        if (lectureHistories == null || lectureHistories.isEmpty()) {
            return new ArrayList<>();
        }

        return lectureHistories;
    }
}