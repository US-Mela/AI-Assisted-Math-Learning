package com.hcmus.mela.history.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AsyncException extends RuntimeException {
    
    private final String errorMessage;
}
