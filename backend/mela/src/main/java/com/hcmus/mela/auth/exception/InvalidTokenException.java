package com.hcmus.mela.auth.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(final String message) {
        super(message);
    }
}
