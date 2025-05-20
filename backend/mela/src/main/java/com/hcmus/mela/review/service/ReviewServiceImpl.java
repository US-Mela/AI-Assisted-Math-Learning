package com.hcmus.mela.review.service;

import com.hcmus.mela.common.utils.ExceptionMessageAccessor;
import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.exercise.dto.dto.ExerciseDto;
import com.hcmus.mela.exercise.service.ExerciseInfoService;
import com.hcmus.mela.exercise.service.ExerciseService;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.dto.dto.TopicDto;
import com.hcmus.mela.lecture.model.Level;
import com.hcmus.mela.lecture.service.LectureListService;
import com.hcmus.mela.lecture.service.LectureService;
import com.hcmus.mela.lecture.service.LevelService;
import com.hcmus.mela.lecture.service.TopicService;
import com.hcmus.mela.review.dto.ExerciseReferenceDto;
import com.hcmus.mela.review.dto.ReviewDto;
import com.hcmus.mela.review.dto.SectionReferenceDto;
import com.hcmus.mela.review.dto.request.UpdateReviewRequest;
import com.hcmus.mela.review.dto.response.GetReviewsResponse;
import com.hcmus.mela.review.dto.response.UpdateReviewResponse;
import com.hcmus.mela.review.mapper.ReviewMapper;
import com.hcmus.mela.review.model.ExerciseReference;
import com.hcmus.mela.review.model.Review;
import com.hcmus.mela.review.model.SectionReference;
import com.hcmus.mela.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final ExerciseService exerciseService;

    private final ExerciseInfoService exerciseInfoService;

    private final LectureListService lectureListService;

    private final LectureService lectureService;

    private final TopicService topicService;

    private final LevelService levelService;

    private final String REVIEWS_FOUND = "reviews_found_successful";

    private final String REVIEW_NOT_FOUND = "review_not_found";

    private final String UPDATE_REVIEW_SUCCESS = "update_review_successful";

    private final GeneralMessageAccessor generalMessageAccessor;

    private final ExceptionMessageAccessor exceptionMessageAccessor;

    private static Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public GetReviewsResponse getReviews(UUID userId) {
        Date startOfDay = truncateTime(new Date());

        Calendar cal = Calendar.getInstance();
        cal.setTime(startOfDay);
        cal.add(Calendar.DATE, 1);
        Date endOfDay = truncateTime(cal.getTime());

        List<Review> reviews = reviewRepository.findAllByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay);

        if (reviews == null || reviews.isEmpty()) {
            reviews = createReview(userId, startOfDay, endOfDay);
        }

        List<ReviewDto> reviewDtos = reviews.stream()
                .map(ReviewMapper.INSTANCE::reviewToReviewDto)
                .toList();

        for (ReviewDto reviewDto : reviewDtos) {
            for (ExerciseReferenceDto exerciseReferenceDto : reviewDto.getExerciseList()) {
                ExerciseDto exerciseDto = exerciseInfoService.findByExerciseId(exerciseReferenceDto.getExerciseId());

                LectureDto lectureDto = lectureService.getLectureById(exerciseDto.getLectureId());

                TopicDto topicDto = topicService.getTopicById(lectureDto.getTopicId());

                Level level = levelService.findLevelByLevelId(lectureDto.getLevelId());

                exerciseReferenceDto.setLectureTitle(lectureDto.getName());
                exerciseReferenceDto.setTopicTitle(topicDto.getName());
                exerciseReferenceDto.setLevelTitle(level.getName());
            }
            for (SectionReferenceDto sectionReferenceDto : reviewDto.getSectionList()) {
                LectureDto lectureDto = lectureService.getLectureById(sectionReferenceDto.getLectureId());

                TopicDto topicDto = topicService.getTopicById(lectureDto.getTopicId());

                Level level = levelService.findLevelByLevelId(lectureDto.getLevelId());

                sectionReferenceDto.setLectureTitle(lectureDto.getName());
                sectionReferenceDto.setTopicTitle(topicDto.getName());
                sectionReferenceDto.setLevelTitle(level.getName());
            }
        }

        final String getReviewsSuccessMessage = generalMessageAccessor.getMessage(null, REVIEWS_FOUND, userId);

        return new GetReviewsResponse(getReviewsSuccessMessage, reviewDtos);
    }

    @Override
    public UpdateReviewResponse updateReview(UUID reviewId, UpdateReviewRequest updateReviewRequest) {
        return null;
    }

    private List<Review> createReview(UUID userId, Date startOfDay, Date endOfDay) {
        List<LectureDto> lectures = lectureListService.getLecturesNeedToBeReviewed(userId);

        int sectionSize = 0;

        int exerciseSize = 0;


        List<Review> results = new ArrayList<>();

        for (LectureDto lecture : lectures) {
            List<ExerciseReference> exerciseReferences = new ArrayList<>();

            List<SectionReference> sectionReferences = new ArrayList<>();

            for (SectionDto sectionDto : lecture.getSections()) {
                sectionReferences.add(new SectionReference(
                        lecture.getLectureId(),
                        sectionDto.getOrdinalNumber(),
                        false));
            }

            for (ExerciseDto exercise : exerciseService.getListOfExercisesInLecture(lecture.getLectureId())) {
                exerciseReferences.add(new ExerciseReference(
                        exercise.getExerciseId(),
                        exercise.getOrdinalNumber(),
                        false));
            }

            Review review = Review.builder()
                    .reviewId(UUID.randomUUID())
                    .userId(userId)
                    .createdAt(startOfDay)
                    .sectionList(sectionReferences)
                    .exerciseList(exerciseReferences)
                    .build();

            results.add(review);

            sectionSize += sectionReferences.size();

            exerciseSize += exerciseReferences.size();

            if (sectionSize >= 3 && exerciseSize >= 3) {
                break;
            }
        }

        if (lectures.isEmpty()) {
            return List.of();
        }

        try {
            return reviewRepository.saveAll(results);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
