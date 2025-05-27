package com.hcmus.mela.ai.client.exception;

import com.hcmus.mela.shared.configuration.RequestIdFilter;
import com.hcmus.mela.shared.exception.ApiErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException e, WebRequest request) {

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                RequestIdFilter.getRequestId(),
                e.getStatusCode(),
                e.getErrorMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(apiErrorResponse.getStatus()).body(apiErrorResponse);
    }
}
