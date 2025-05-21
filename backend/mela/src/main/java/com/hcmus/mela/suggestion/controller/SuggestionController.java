package com.hcmus.mela.suggestion.controller;

import com.hcmus.mela.auth.security.jwt.JwtTokenService;
import com.hcmus.mela.suggestion.dto.request.UpdateSuggestionRequest;
import com.hcmus.mela.suggestion.dto.response.GetSuggestionsResponse;
import com.hcmus.mela.suggestion.dto.response.UpdateSuggestionResponse;
import com.hcmus.mela.suggestion.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suggestions")
@Slf4j
public class SuggestionController {

    private final SuggestionService suggestionService;

    private final JwtTokenService jwtTokenService;

    @GetMapping
    @Operation(
            tags = "Suggestion Service",
            summary = "Get suggestions by user ID",
            description = "Retrieves all suggestions of the user with given user ID."
    )
    public ResponseEntity<GetSuggestionsResponse> getSuggestions(@RequestHeader("Authorization") String authorizationHeader) {
        UUID userId = jwtTokenService.getUserIdFromAuthorizationHeader(authorizationHeader);

        log.info("Get suggestion sections and exercises for user {}.", userId);

        GetSuggestionsResponse response = suggestionService.getSuggestions(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{suggestionId}")
    @Operation(
            tags = "Suggestion Service",
            summary = "Update suggestion with suggestion ID",
            description = "Update suggestion that has given ID."
    )
    public ResponseEntity<UpdateSuggestionResponse> updateSuggestion(@RequestHeader("Authorization") String authorizationHeader,
                                                                     @PathVariable("suggestionId") String suggestionId,
                                                                     @RequestBody UpdateSuggestionRequest request) {
        log.info("Update suggestion with suggestion ID {}.", suggestionId);

        UpdateSuggestionResponse response = suggestionService.updateSuggestion(UUID.fromString(suggestionId), request);

        return ResponseEntity.ok(response);
    }
}
