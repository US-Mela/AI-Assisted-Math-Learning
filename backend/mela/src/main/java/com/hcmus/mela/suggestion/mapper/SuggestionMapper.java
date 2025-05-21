package com.hcmus.mela.suggestion.mapper;

import com.hcmus.mela.suggestion.dto.SuggestionDto;
import com.hcmus.mela.suggestion.model.Suggestion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SuggestionMapper {
    SuggestionMapper INSTANCE = Mappers.getMapper(SuggestionMapper.class);

    SuggestionDto suggestionToSuggestionDto(Suggestion suggestion);
}
