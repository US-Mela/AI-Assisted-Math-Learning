package com.hcmus.mela.exceptions.advice;

import com.hcmus.mela.controller.RefreshTokenController;
import com.hcmus.mela.exceptions.custom.InvalidTokenException;
import com.hcmus.mela.exceptions.response.ApiExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackageClasses = RefreshTokenController.class)
public class RefreshTokenControllerAdvice {
    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<ApiExceptionResponse> handleRefreshTokenException(InvalidTokenException exception) {

        final ApiExceptionResponse response = new ApiExceptionResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now());

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}