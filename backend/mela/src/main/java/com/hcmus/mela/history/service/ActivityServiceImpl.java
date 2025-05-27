package com.hcmus.mela.history.service;

import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.history.dto.dto.RecentActivityDto;
import com.hcmus.mela.history.model.LectureByTime;
import com.hcmus.mela.history.repository.ExerciseHistoryRepository;
import com.hcmus.mela.history.repository.LectureHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ExerciseHistoryRepository exerciseHistoryRepository;

    private final LectureHistoryRepository lectureHistoryRepository;

    private final AsyncCustomService asyncService;

    @Override
    public List<RecentActivityDto> getRecentActivityOfUser(UUID userId, Integer size) {
        CompletableFuture<List<LectureByTime>> sectionsFuture = asyncService.runAsync(
                () -> lectureHistoryRepository.getRecentLecturesBySectionOfUser(userId),
                Collections.emptyList());

        CompletableFuture<List<LectureByTime>> exercisesFuture = asyncService.runAsync(
                () -> exerciseHistoryRepository.getRecentLecturesByExercisesOfUser(userId),
                Collections.emptyList());

        List<LectureByTime> sectionsByTimeList = sectionsFuture.join();
        List<LectureByTime> exercisesByTimeList = exercisesFuture.join();

        Map<UUID, LocalDateTime> lectureCompletedAtMap = mergeLectureCompletionTimes(sectionsByTimeList, exercisesByTimeList);

        if (lectureCompletedAtMap == null || lectureCompletedAtMap.isEmpty()) {
            return Collections.emptyList();
        }

        return lectureCompletedAtMap.entrySet().stream()
                .map(entry -> RecentActivityDto.builder()
                        .lectureId(entry.getKey())
                        .date(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(RecentActivityDto::getDate).reversed())
                .limit(size)
                .toList();
    }

    private Map<UUID, LocalDateTime> mergeLectureCompletionTimes(
            List<LectureByTime> sectionsByTimeList,
            List<LectureByTime> exercisesByTimeList) {
        Map<UUID, LocalDateTime> lectureCompletedAtMap = new HashMap<>();

        if (sectionsByTimeList != null && !sectionsByTimeList.isEmpty()) {
            lectureCompletedAtMap = sectionsByTimeList.stream()
                    .collect(Collectors.toMap(LectureByTime::getLectureId, LectureByTime::getCompletedAt));
        }

        for (LectureByTime lectureByTime : exercisesByTimeList) {
            lectureCompletedAtMap.merge(lectureByTime.getLectureId(), lectureByTime.getCompletedAt(),
                    (existingDate, newDate) -> existingDate.isAfter(newDate) ? newDate : existingDate);
        }

        return lectureCompletedAtMap;
    }
}
