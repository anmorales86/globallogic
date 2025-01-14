package com.anderson.globallogic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CustomSecurityException extends RuntimeException {

    public CustomSecurityException(String message) {
        super(message);
    }

    public CustomSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}