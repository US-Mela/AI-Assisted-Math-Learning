package com.hcmus.mela.suggestion.service;

import com.hcmus.mela.common.utils.ExceptionMessageAccessor;
import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.history.model.LectureCompletedSection;
import com.hcmus.mela.history.model.LectureHistory;
import com.hcmus.mela.history.service.LectureHistoryService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.service.LectureService;
import com.hcmus.mela.lecture.service.LevelService;
import com.hcmus.mela.lecture.service.TopicService;
import com.hcmus.mela.suggestion.dto.SectionReferenceDto;
import com.hcmus.mela.suggestion.dto.SuggestionDto;
import com.hcmus.mela.suggestion.dto.request.UpdateSuggestionRequest;
import com.hcmus.mela.suggestion.dto.response.GetSuggestionsResponse;
import com.hcmus.mela.suggestion.dto.response.UpdateSuggestionResponse;
import com.hcmus.mela.suggestion.exception.SuggestionNotFoundException;
import com.hcmus.mela.suggestion.mapper.SuggestionMapper;
import com.hcmus.mela.suggestion.model.SectionReference;
import com.hcmus.mela.suggestion.model.Suggestion;
import com.hcmus.mela.suggestion.repository.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestionServiceImpl implements SuggestionService {

    private final LectureHistoryService lectureHistoryService;

    private final LectureService lectureService;

    private final SuggestionRepository suggestionRepository;

    private final TopicService topicService;

    private final LevelService levelService;

    private final GeneralMessageAccessor generalMessageAccessor;

    private final String SUGGESTIONS_FOUND = "suggestions_found_successful";

    private final String SUGGESTION_NOT_FOUND = "suggestion_not_found";

    private final String UPDATE_SUGGESTION_SUCCESS = "update_suggestion_successful";
    private final ExceptionMessageAccessor exceptionMessageAccessor;

    @Override
    public GetSuggestionsResponse getSuggestions(UUID userId) {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfDayUtc = utcNow.toLocalDate().atStartOfDay(ZoneOffset.UTC);

        Date startOfDay = Date.from(startOfDayUtc.toInstant());

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(startOfDay);
        cal.add(Calendar.DATE, 1);

        Date endOfDay = cal.getTime();

        List<Suggestion> suggestions = suggestionRepository.findAllByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        if (suggestions == null || suggestions.isEmpty()) {
            suggestions = createSuggestion(userId, startOfDay);
        }

        List<SuggestionDto> suggestionDtos = suggestions.stream()
                .map(SuggestionMapper.INSTANCE::suggestionToSuggestionDto)
                .toList();

        for (SuggestionDto suggestionDto : suggestionDtos) {
            for (SectionReferenceDto sectionReferenceDto : suggestionDto.getSectionList()) {
                LectureDto lectureDto = lectureService.getLectureById(sectionReferenceDto.getLectureId());

                TopicDto topicDto = topicService.getTopicById(lectureDto.getTopicId());

                Level level = levelService.findLevelByLevelId(lectureDto.getLevelId());

                sectionReferenceDto.setLectureTitle(lectureDto.getName());
                sectionReferenceDto.setTopicTitle(topicDto.getName());
                sectionReferenceDto.setLevelTitle(level.getName());
            }
        }
        final String getSuggestionsSuccessMessage = generalMessageAccessor.getMessage(null, SUGGESTIONS_FOUND, userId);

        return new GetSuggestionsResponse(getSuggestionsSuccessMessage, suggestionDtos);
    }

    @Override
    public UpdateSuggestionResponse updateSuggestion(UUID suggestionId, UpdateSuggestionRequest updateSuggestionRequest) {
        Suggestion suggestion = suggestionRepository.findBySuggestionId(suggestionId);

        if (suggestion == null) {
            final String errorMessage = exceptionMessageAccessor.getMessage(null, SUGGESTION_NOT_FOUND, suggestionId);
            log.error(errorMessage);
            throw new SuggestionNotFoundException(errorMessage);
        }

        for (SectionReference sectionReference : suggestion.getSectionList()) {
            if (sectionReference.getLectureId().equals(updateSuggestionRequest.getItemId())
                    && sectionReference.getOrdinalNumber().equals(updateSuggestionRequest.getOrdinalNumber())) {
                sectionReference.setIsDone(updateSuggestionRequest.getIsDone());
                break;
            }
        }

        Suggestion result = suggestionRepository.updateSuggestion(suggestion);

        final String updateSuccessMessage = generalMessageAccessor.getMessage(null, UPDATE_SUGGESTION_SUCCESS, suggestionId);

        return new UpdateSuggestionResponse(updateSuccessMessage);
    }

    private List<Suggestion> createSuggestion(UUID userId, Date startOfDay) {
        List<LectureHistory> lectureHistories = lectureHistoryService.getBestProgressHistoriesGroupedByLecture(userId);

        int sectionSize = 0;

        List<Suggestion> results = new ArrayList<>();
        List<LectureDto> suggestedLectures = new ArrayList<>();


        List<UUID> studiedLectureIds = lectureHistories.stream()
                .map(LectureHistory::getLectureId)
                .toList();

        for (LectureHistory history : lectureHistories) {
            List<SectionReference> sectionReferences = new ArrayList<>();
            boolean isSuggested = false;

            if (history.getProgress() < 100) {
                LectureDto lecture = lectureService.getLectureById(history.getLectureId());

                List<Integer> completedSectionNumbers = history.getCompletedSections().stream()
                        .map(LectureCompletedSection::getOrdinalNumber)
                        .toList();

                for (SectionDto section : lecture.getSections()) {

                    if (!completedSectionNumbers.contains(section.getOrdinalNumber())) {

                        sectionReferences.add(new SectionReference(lecture.getLectureId(),
                                section.getOrdinalNumber(),
                                false));

                        isSuggested = true;
                    }
                }

                if (isSuggested) {
                    suggestedLectures.add(lecture);
                }
            } else {
                LectureDto lecture = lectureService.getLectureByTopicIdAndLevelIdAndOrdinalNumber(
                        history.getTopicId(),
                        history.getLevelId(),
                        lectureService.getLectureOrdinalNumber(history.getLectureId()) + 1);

                if (lecture != null) {

                    if (!studiedLectureIds.contains(lecture.getLectureId())) {

                        for (SectionDto section : lecture.getSections()) {

                            sectionReferences.add(new SectionReference(
                                    lecture.getLectureId(),
                                    section.getOrdinalNumber(),
                                    false));
                        }

                        suggestedLectures.add(lecture);
                    }
                }
            }

            if (!sectionReferences.isEmpty()) {
                Suggestion suggestion = Suggestion.builder()
                        .suggestionId(UUID.randomUUID())
                        .userId(userId)
                        .createdAt(startOfDay)
                        .sectionList(sectionReferences)
                        .build();

                results.add(suggestion);

                sectionSize += sectionReferences.size();
            }

            if (sectionSize >= 3) {
                break;
            }
        }

        if (sectionSize < 3 && !suggestedLectures.isEmpty()) {
            List<UUID> suggestedLectureIds = new ArrayList<>(suggestedLectures.stream()
                    .map(LectureDto::getLectureId)
                    .toList());

            for (int i = 0; i < suggestedLectureIds.size(); i++) {
                LectureDto lecture = suggestedLectures.get(i);
                List<SectionReference> sectionReferences = new ArrayList<>();

                LectureDto nextLecture = lectureService.getLectureByTopicIdAndLevelIdAndOrdinalNumber(
                        lecture.getTopicId(),
                        lecture.getLevelId(),
                        lectureService.getLectureOrdinalNumber(lecture.getLectureId()) + 1);

                if (nextLecture != null) {

                    if (!suggestedLectureIds.contains(nextLecture.getLectureId())) {

                        for (SectionDto section : nextLecture.getSections()) {

                            sectionReferences.add(new SectionReference(
                                    nextLecture.getLectureId(),
                                    section.getOrdinalNumber(),
                                    false));
                        }

                        suggestedLectures.add(nextLecture);

                        suggestedLectureIds.add(nextLecture.getLectureId());

                        Suggestion suggestion = Suggestion.builder()
                                .suggestionId(UUID.randomUUID())
                                .userId(userId)
                                .createdAt(startOfDay)
                                .sectionList(sectionReferences)
                                .build();

                        results.add(suggestion);

                        sectionSize += sectionReferences.size();
                    }
                }



                if (sectionSize >= 3) {
                    break;
                }
            }
        }

        if (lectureHistories.isEmpty()) {
            return List.of();
        }

        try {
            return suggestionRepository.saveAll(results);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
