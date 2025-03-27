package com.hcmus.mela.statistic.exception;

import com.hcmus.mela.common.exception.ApiErrorResponse;
import com.hcmus.mela.statistic.controller.StatisticController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestControllerAdvice(basePackageClasses = StatisticController.class)
public class StatisticExceptionHandler {

    private String getRequestId() {
        String requestId = MDC.get("X-Request-Id");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    @ExceptionHandler(StatisticException.class)
    ResponseEntity<ApiErrorResponse> handleMathContentException(StatisticException exception, WebRequest request) {
        final ApiErrorResponse response = new ApiErrorResponse(
                getRequestId(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
