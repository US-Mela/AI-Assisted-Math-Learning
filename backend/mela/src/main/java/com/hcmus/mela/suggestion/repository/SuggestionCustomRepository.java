package com.hcmus.mela.suggestion.repository;

import com.hcmus.mela.suggestion.model.Suggestion;

public interface SuggestionCustomRepository {
    Suggestion updateSuggestion(Suggestion suggestion);
}
