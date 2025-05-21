package com.hcmus.mela.ai.question.hint.controller;

import com.hcmus.mela.ai.question.hint.dto.response.HintResponseDto;
import com.hcmus.mela.ai.question.hint.service.QuestionHintService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class QuestionHintController {

    private final QuestionHintService questionHintService;

    @PostMapping("/{questionId}/hint/terms")
    public ResponseEntity<HintResponseDto> generateTerms(@PathVariable UUID questionId,
                                                        @RequestHeader(value = "Authorization") String authorizationHeader) {

        HintResponseDto hint = questionHintService.generateTerms(questionId);

        return ResponseEntity.ok(hint);
    }

    @PostMapping("/{questionId}/hint/guide")
    public ResponseEntity<HintResponseDto> generateGuide(@PathVariable UUID questionId,
                                                         @RequestHeader(value = "Authorization") String authorizationHeader) {

        HintResponseDto hint = questionHintService.generateGuide(questionId);

        return ResponseEntity.ok(hint);
    }
}


