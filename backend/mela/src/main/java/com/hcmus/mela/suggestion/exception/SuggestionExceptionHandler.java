package com.hcmus.mela.suggestion.exception;

import com.hcmus.mela.common.configuration.RequestIdFilter;
import com.hcmus.mela.common.exception.ApiErrorResponse;
import com.hcmus.mela.suggestion.controller.SuggestionController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = SuggestionController.class)
public class SuggestionExceptionHandler {
    @ExceptionHandler(SuggestionNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleSuggestionNotFoundException(SuggestionNotFoundException ex, WebRequest request) {
        final ApiErrorResponse response = new ApiErrorResponse(
                RequestIdFilter.getRequestId(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
