package com.hcmus.mela.history.mapper;

import com.hcmus.mela.history.dto.dto.RecentActivityDto;
import com.hcmus.mela.history.model.ExerciseHistory;
import com.hcmus.mela.history.model.LectureByTime;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecentActivityMapper {

    RecentActivityMapper INSTANCE = Mappers.getMapper(RecentActivityMapper.class);

    @Mapping(source = "completedAt", target = "date")
    RecentActivityDto convertToRecentActivityDto(ExerciseHistory exerciseHistory);
}