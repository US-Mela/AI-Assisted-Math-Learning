package com.hcmus.mela.statistic.exception.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AsyncException extends RuntimeException {
    
    private final String errorMessage;
}
