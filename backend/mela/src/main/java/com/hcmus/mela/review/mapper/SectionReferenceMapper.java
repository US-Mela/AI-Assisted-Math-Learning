package com.hcmus.mela.review.mapper;

import com.hcmus.mela.review.dto.SectionReferenceDto;
import com.hcmus.mela.review.model.SectionReference;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectionReferenceMapper {
    SectionReferenceMapper INSTANCE = Mappers.getMapper(SectionReferenceMapper.class);

    SectionReferenceDto sectionReferenceToSectionReferenceDto(SectionReference sectionReference);

    SectionReference sectionReferenceDtoToSectionReference(SectionReferenceDto sectionReferenceDto);
}
