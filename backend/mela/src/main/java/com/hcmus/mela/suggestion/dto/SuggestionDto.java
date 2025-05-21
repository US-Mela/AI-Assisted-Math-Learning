package com.hcmus.mela.suggestion.dto;

import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionDto {
    private UUID suggestionId;

    private UUID userId;

    private Date createdAt;

    private List<SectionReferenceDto> sectionList;
}
