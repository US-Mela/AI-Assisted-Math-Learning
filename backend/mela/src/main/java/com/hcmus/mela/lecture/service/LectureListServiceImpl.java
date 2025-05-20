package com.hcmus.mela.lecture.service;

import com.hcmus.mela.common.async.AsyncCustomService;
import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.history.service.ExerciseHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.LectureStatDetailDto;
import com.hcmus.mela.lecture.dto.dto.LecturesByTopicDto;
import com.hcmus.mela.lecture.dto.response.GetLecturesByLevelResponse;
import com.hcmus.mela.lecture.dto.response.GetLecturesResponse;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.TopicMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.model.LectureActivity;
import com.hcmus.mela.lecture.repository.LectureCustomRepositoryImpl;
import com.hcmus.mela.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureListServiceImpl implements LectureListService {

    private final GeneralMessageAccessor generalMessageAccessor;

    private final TopicService topicService;

    private final LectureRepository lectureRepository;

    private final ExerciseHistoryService exerciseHistoryService;

    private final AsyncCustomService asyncService;
    private final LectureCustomRepositoryImpl lectureCustomRepositoryImpl;

    @Override
    public GetLecturesByLevelResponse getLecturesByLevel(UUID userId, UUID levelId) {
        CompletableFuture<Map<UUID, Integer>> passExerciseTotalsMapFuture = asyncService.runAsync(
                () -> exerciseHistoryService.getPassedExerciseCountOfUser(userId),
                Collections.emptyMap());
        CompletableFuture<List<LecturesByTopicDto>> lecturesByTopicDtoListFuture = asyncService.runAsync(
                () -> topicService.getTopics()
                        .stream()
                        .map(TopicMapper.INSTANCE::topicDtoToLecturesByTopicDto)
                        .toList(),
                Collections.emptyList());

        CompletableFuture.allOf(passExerciseTotalsMapFuture, lecturesByTopicDtoListFuture).join();

        Map<UUID, Integer> passMap = passExerciseTotalsMapFuture.join();
        List<LecturesByTopicDto> lecturesByTopicDtoList = lecturesByTopicDtoListFuture.join();

        for (LecturesByTopicDto lecturesByTopicDto : lecturesByTopicDtoList) {
            List<Lecture> lectures = lectureRepository.findLecturesByTopicAndLevel(lecturesByTopicDto.getTopicId(), levelId);
            if (lectures == null || lectures.isEmpty()) {
                lecturesByTopicDto.setLectures(Collections.emptyList());
                continue;
            }
            List<LectureStatDetailDto> lectureStatDetailDtoList = convertLecturesToLectureStatList(passMap, lectures);
            lecturesByTopicDto.setLectures(lectureStatDetailDtoList);
        }
        lecturesByTopicDtoList = lecturesByTopicDtoList
                .stream().filter(dto -> !dto.getLectures().isEmpty()).toList();

        return new GetLecturesByLevelResponse(
                generalMessageAccessor.getMessage(null, "get_lectures_success"),
                lecturesByTopicDtoList.size(),
                lecturesByTopicDtoList
        );
    }

    @Override
    public GetLecturesResponse getLecturesByKeyword(UUID userId, String keyword) {
        CompletableFuture<List<Lecture>> lecturesFuture = asyncService.runAsync(
                () -> lectureRepository.findLecturesByKeyword(keyword),
                Collections.emptyList());
        CompletableFuture<Map<UUID, Integer>> passExerciseTotalsMapFuture = asyncService.runAsync(
                () -> exerciseHistoryService.getPassedExerciseCountOfUser(userId),
                Collections.emptyMap());

        CompletableFuture.allOf(passExerciseTotalsMapFuture, lecturesFuture).join();

        Map<UUID, Integer> passMap = passExerciseTotalsMapFuture.join();
        List<Lecture> lectures = lecturesFuture.join();

        if (lectures == null || lectures.isEmpty()) {
            return new GetLecturesResponse(
                    generalMessageAccessor.getMessage(null, "search_lectures_success"),
                    0,
                    Collections.emptyList()
            );
        }
        return getLectureStatListResponse(passMap, lectures);
    }

    @Override
    public GetLecturesResponse getLecturesByRecent(UUID userId, Integer size) {
        CompletableFuture<List<LectureActivity>> exerciseHistoryFuture = asyncService.runAsync(
                () -> lectureRepository.findRecentLectureByUserExerciseHistory(userId, size),
                Collections.emptyList());
        CompletableFuture<List<LectureActivity>> sectionHistoryFuture = asyncService.runAsync(
                () -> lectureRepository.findRecentLectureByUserSectionHistory(userId, size),
                Collections.emptyList());
        CompletableFuture<Map<UUID, Integer>> passMapFuture = asyncService.runAsync(
                () -> exerciseHistoryService.getPassedExerciseCountOfUser(userId),
                Collections.emptyMap());
        CompletableFuture.allOf(exerciseHistoryFuture, sectionHistoryFuture, passMapFuture).join();

        List<LectureActivity> exerciseHistory = Optional.ofNullable(exerciseHistoryFuture.join()).orElse(Collections.emptyList());
        List<LectureActivity> sectionHistory = Optional.ofNullable(sectionHistoryFuture.join()).orElse(Collections.emptyList());

        List<LectureActivity> recentLectures = mergeActivity(exerciseHistory, sectionHistory);
        if (recentLectures.isEmpty()) {
            return new GetLecturesResponse(
                    generalMessageAccessor.getMessage(null, "get_recent_lectures_success"),
                    0,
                    Collections.emptyList()
            );
        }
        List<Lecture> lectures = recentLectures.stream()
                .sorted(Comparator.comparing(LectureActivity::getCompletedAt).reversed())
                .limit(size)
                .map(LectureMapper.INSTANCE::lectureActivityToLecture)
                .toList();

        return getLectureStatListResponse(passMapFuture.join(), lectures);
    }

    @Override
    public List<LectureDto> getLecturesNeedToBeReviewed(UUID userId) {
        List<Lecture> lectures = lectureCustomRepositoryImpl.findCompleteLecturesWithWrongExercises(userId);

        return lectures.stream()
                .map(LectureMapper.INSTANCE::lectureToLectureDto)
                .toList();
    }

    private List<LectureActivity> mergeActivity(List<LectureActivity> first, List<LectureActivity> second) {
        List<LectureActivity> recentLecture = new ArrayList<>();

        List<LectureActivity> modifiableFirst = new ArrayList<>(first);
        List<LectureActivity> modifiableSecond = new ArrayList<>(second);
        modifiableFirst.sort(Comparator.comparing(LectureActivity::getLectureId));
        modifiableSecond.sort(Comparator.comparing(LectureActivity::getLectureId));

        int n = modifiableFirst.size() + modifiableSecond.size();
        int firstIndex = 0;
        int secondIndex = 0;
        while (firstIndex + secondIndex <= n && firstIndex < modifiableFirst.size() && secondIndex < modifiableSecond.size()) {
            if (modifiableFirst.get(firstIndex).compareTo(modifiableSecond.get(secondIndex)) < 0) {
                recentLecture.add(modifiableFirst.get(firstIndex));
                firstIndex++;
            } else if (modifiableFirst.get(firstIndex).compareTo(modifiableSecond.get(secondIndex)) > 0) {
                recentLecture.add(modifiableSecond.get(secondIndex));
                secondIndex++;
            } else if (modifiableFirst.get(firstIndex++).getCompletedAt()
                    .isAfter(modifiableSecond.get(secondIndex++).getCompletedAt())) {
                recentLecture.add(modifiableFirst.get(firstIndex - 1));
            } else {
                recentLecture.add(modifiableSecond.get(secondIndex - 1));
            }
        }

        if (firstIndex < modifiableFirst.size()) {
            recentLecture.addAll(modifiableFirst.subList(firstIndex, modifiableFirst.size()));
        }
        if (secondIndex < modifiableSecond.size()) {
            recentLecture.addAll(modifiableSecond.subList(secondIndex, modifiableSecond.size()));
        }
        recentLecture.sort(Comparator.comparing(LectureActivity::getCompletedAt).reversed());
        return recentLecture;
    }

    private GetLecturesResponse getLectureStatListResponse(Map<UUID, Integer> passExerciseTotalsMap, List<Lecture> lectures) {
        List<LectureStatDetailDto> lectureStatDetailDtoList = convertLecturesToLectureStatList(
                passExerciseTotalsMap,
                lectures);
        return new GetLecturesResponse(
                "Get lectures success!",
                lectureStatDetailDtoList.size(),
                lectureStatDetailDtoList);
    }

    private List<LectureStatDetailDto> convertLecturesToLectureStatList(Map<UUID, Integer> passExerciseTotalsMap, List<Lecture> lectures) {
        List<LectureStatDetailDto> lectureStatDetailDtoList = new ArrayList<>();
        for (Lecture lecture : lectures) {
            LectureStatDetailDto lectureStatDetailDto = LectureMapper.INSTANCE.lectureToLectureStatDetailDto(lecture);
            lectureStatDetailDto.setTotalPassExercises(passExerciseTotalsMap.getOrDefault(lecture.getLectureId(), 0));
            lectureStatDetailDtoList.add(lectureStatDetailDto);
        }
        return lectureStatDetailDtoList;
    }
}