package com.hcmus.mela.suggestion.service;

import com.hcmus.mela.suggestion.dto.request.UpdateSuggestionRequest;
import com.hcmus.mela.suggestion.dto.response.GetSuggestionsResponse;
import com.hcmus.mela.suggestion.dto.response.UpdateSuggestionResponse;

import java.util.Date;
import java.util.UUID;

public interface SuggestionService {
    GetSuggestionsResponse getSuggestions(UUID userId);

    UpdateSuggestionResponse updateSuggestion(UUID suggestionId, UpdateSuggestionRequest updateSuggestionRequest);
}
