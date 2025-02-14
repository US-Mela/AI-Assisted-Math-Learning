package com.hcmus.mela.auth.exception.advice;

import com.hcmus.mela.auth.controller.AuthController;
import com.hcmus.mela.auth.exception.exception.InvalidTokenException;
import com.hcmus.mela.auth.exception.exception.RegistrationException;
import com.hcmus.mela.auth.exception.exception.UserNotFoundException;
import com.hcmus.mela.auth.exception.response.ApiExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthControllerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiExceptionResponse> handleUserNotFoundException(UserNotFoundException exception) {

        final ApiExceptionResponse response = new ApiExceptionResponse(exception.getErrorMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<ApiExceptionResponse> handleInvalidTokenException(InvalidTokenException exception) {

        final ApiExceptionResponse response = new ApiExceptionResponse(exception.getErrorMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now());

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(RegistrationException.class)
    ResponseEntity<ApiExceptionResponse> handleLoginException(RegistrationException exception) {

        final ApiExceptionResponse response = new ApiExceptionResponse(exception.getErrorMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiExceptionResponse> handleLoginException(BadCredentialsException exception) {

        final ApiExceptionResponse response = new ApiExceptionResponse("Invalid username or password", HttpStatus.UNAUTHORIZED, LocalDateTime.now());

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
