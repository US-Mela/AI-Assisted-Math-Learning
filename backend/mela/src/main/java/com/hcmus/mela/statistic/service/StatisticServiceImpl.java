package com.hcmus.mela.statistic.service;

import com.hcmus.mela.shared.async.AsyncCustomService;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.history.dto.dto.ExerciseHistoryDto;
import com.hcmus.mela.history.dto.dto.LectureCompletedSectionDto;
import com.hcmus.mela.history.dto.dto.LectureHistoryDto;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.history.service.LectureHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.service.LectureService;
import com.hcmus.mela.lecture.service.TopicService;
import com.hcmus.mela.statistic.dto.dto.*;
import com.hcmus.mela.statistic.dto.response.GetStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final LectureHistoryService lectureHistoryService;

    private final TopicService topicService;

    private final LectureService lectureService;

    private final ExerciseInfoService exerciseInfoService;

    private final ExerciseHistoryService exerciseHistoryService;

    private final AsyncCustomService asyncService;

    public GetStatisticsResponse getStatisticByUserAndLevelAndType(UUID userId, UUID levelId, ActivityType activityType) {
        List<ActivityHistoryDto> activityHistoryDtoList = new ArrayList<>();

        switch (activityType) {
            case SECTION:
                activityHistoryDtoList.addAll(getActivityFromLectureHistory(userId, levelId));
                break;
            case EXERCISE:
                activityHistoryDtoList.addAll(getActivityFromExerciseHistory(userId, levelId));
                break;
            default:
                CompletableFuture<List<ActivityHistoryDto>> lectureActivitiesFuture =
                        asyncService.runAsync(() -> getActivityFromLectureHistory(userId, levelId), Collections.emptyList());
                CompletableFuture<List<ActivityHistoryDto>> exerciseActivitiesFuture =
                        asyncService.runAsync(() -> getActivityFromExerciseHistory(userId, levelId), Collections.emptyList());

                CompletableFuture.allOf(lectureActivitiesFuture, exerciseActivitiesFuture).join();

                activityHistoryDtoList.addAll(lectureActivitiesFuture.join());
                activityHistoryDtoList.addAll(exerciseActivitiesFuture.join());
                break;
        }
        activityHistoryDtoList.sort(Comparator.comparing(ActivityHistoryDto::getLatestDate).reversed());

        return new GetStatisticsResponse(
                "Get statistic successfully for user " + userId + " and level " + levelId + "!",
                activityHistoryDtoList.size(),
                activityHistoryDtoList);
    }

    private List<ActivityHistoryDto> getActivityFromLectureHistory(UUID userId, UUID levelId) {
        Map<UUID, String> topicNameMap = getTopicNameMap();

        List<LectureHistoryDto> lectureHistories = lectureHistoryService.getLectureHistoryByUserAndLevel(userId, levelId);
        if (lectureHistories.isEmpty()) {
            return Collections.emptyList();
        }

        List<ActivityHistoryDto> activities = new ArrayList<>();

        for (LectureHistoryDto lectureHistory : lectureHistories) {
            LectureDto lecture = lectureService.getLectureById(lectureHistory.getLectureId());
            String lectureName = lecture.getName();
            String topicName = topicNameMap.get(lecture.getTopicId());

            Map<Integer, String> sectionNameMap = lecture.getSections().stream()
                    .collect(Collectors.toMap(SectionDto::getOrdinalNumber, SectionDto::getName));

            for (LectureCompletedSectionDto section : lectureHistory.getCompletedSections()) {
                ActivityHistoryDto activity = new ActivityHistoryDto();
                activity.setType(ActivityType.SECTION);
                activity.setLatestDate(section.getCompletedAt());
                activity.setTopicName(topicName);
                activity.setLectureName(lectureName);
                activity.setSection(new SectionActivityDto(
                        sectionNameMap.get(section.getOrdinalNumber()),
                        section.getCompletedAt()));

                activities.add(activity);
            }
        }

        return activities;
    }

    private List<ActivityHistoryDto> getActivityFromExerciseHistory(UUID userId, UUID levelId) {
        Map<UUID, String> topicNameMap = getTopicNameMap();

        List<ExerciseHistoryDto> exerciseHistories = exerciseHistoryService.getExerciseHistoryByUserAndLevel(userId, levelId);
        if (exerciseHistories.isEmpty()) {
            return Collections.emptyList();
        }
        Map<UUID, List<ExerciseHistoryDto>> exerciseHistoriesByExerciseId = exerciseHistories.stream()
                .collect(Collectors.groupingBy(ExerciseHistoryDto::getExerciseId));

        List<ActivityHistoryDto> activities = new ArrayList<>();

        exerciseHistoriesByExerciseId.forEach((exerciseId, histories) -> {
            ExerciseHistoryDto firstHistory = histories.get(0);
            LectureDto lecture = lectureService.getLectureById(firstHistory.getLectureId());
            ExerciseDto exercise = exerciseInfoService.findByExerciseId(exerciseId);

            ActivityHistoryDto activity = new ActivityHistoryDto();
            activity.setType(ActivityType.EXERCISE);
            activity.setTopicName(topicNameMap.get(lecture.getTopicId()));
            activity.setLectureName(lecture.getName());

            ExerciseActivityDto exerciseActivity = new ExerciseActivityDto();
            exerciseActivity.setExerciseName(exercise.getExerciseName());

            List<ScoreRecordDto> scoreRecords = histories.stream()
                    .map(history -> new ScoreRecordDto(history.getCompletedAt(), history.getScore()))
                    .sorted(Comparator.comparing(ScoreRecordDto::getDate).reversed())
                    .collect(Collectors.toList());

            exerciseActivity.setScoreRecords(scoreRecords);

            if (!scoreRecords.isEmpty()) {
                ScoreRecordDto latestScore = scoreRecords.get(0);
                exerciseActivity.setLatestScore(latestScore.getScore());
                activity.setLatestDate(latestScore.getDate());
            }

            activity.setExercise(exerciseActivity);
            activities.add(activity);
        });

        return activities;
    }

    private Map<UUID, String> getTopicNameMap() {
        return topicService.getTopics().stream()
                .collect(Collectors.toMap(TopicDto::getTopicId, TopicDto::getName));
    }
}