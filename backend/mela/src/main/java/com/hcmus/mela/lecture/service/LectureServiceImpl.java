package com.hcmus.mela.lecture.service;

import com.hcmus.mela.common.utils.GeneralMessageAccessor;
import com.hcmus.mela.lecture.dto.dto.LectureDto;
import com.hcmus.mela.lecture.dto.dto.LectureOfSectionDto;
import com.hcmus.mela.lecture.dto.dto.SectionDto;
import com.hcmus.mela.lecture.dto.response.GetLectureSectionsResponse;
import com.hcmus.mela.lecture.mapper.LectureMapper;
import com.hcmus.mela.lecture.mapper.LectureSectionMapper;
import com.hcmus.mela.lecture.model.Lecture;
import com.hcmus.mela.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final GeneralMessageAccessor generalMessageAccessor;

    private final LectureRepository lectureRepository;

    @Override
    public LectureDto getLectureById(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);
        if (lecture == null) {
            return null;
        }
        return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }

    @Override
    public Integer getLectureOrdinalNumber(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);

        if (lecture == null) {
            return -1;
        }

        return lecture.getOrdinalNumber();
    }

    @Override
    public GetLectureSectionsResponse getLectureSections(UUID lectureId) {
        Lecture lecture = lectureRepository.findByLectureId(lectureId);
        if (lecture == null) {
            return new GetLectureSectionsResponse(
                    generalMessageAccessor.getMessage(null, "get_sections_success"),
                    0,
                    null,
                    Collections.emptyList()
            );
        }
        LectureOfSectionDto lectureInfo = LectureMapper.INSTANCE.lectureToLectureOfSectionDto(lecture);
        if (lecture.getSections() == null) {
            return new GetLectureSectionsResponse(
                    generalMessageAccessor.getMessage(null, "get_sections_success"),
                    0,
                    lectureInfo,
                    Collections.emptyList()
            );
        }
        List<SectionDto> sectionDtoList = lecture.getSections().stream()
                .map(LectureSectionMapper.INSTANCE::lectureSectionToLectureSectionDto)
                .sorted(Comparator.comparingInt(SectionDto::getOrdinalNumber))
                .toList();

        return new GetLectureSectionsResponse(
                generalMessageAccessor.getMessage(null, "get_sections_success"),
                sectionDtoList.size(),
                lectureInfo,
                sectionDtoList
        );
    }

    @Override
    public LectureDto getLectureByTopicIdAndLevelIdAndOrdinalNumber(UUID topicId, UUID levelId, Integer ordinalNumber) {
        Lecture lecture = lectureRepository.findByTopicIdAndLevelIdAndOrdinalNumber(topicId, levelId, ordinalNumber);

        if (lecture == null) {
            return null;
        }

        return LectureMapper.INSTANCE.lectureToLectureDto(lecture);
    }
}
