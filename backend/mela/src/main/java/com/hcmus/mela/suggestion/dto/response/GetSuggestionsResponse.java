package com.hcmus.mela.suggestion.dto.response;

import com.hcmus.mela.suggestion.dto.SuggestionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSuggestionsResponse {
    private String message;

    private List<SuggestionDto> suggestions;
}
