package com.learn.exception;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public ApplicationException(@NonNull HttpStatus httpStatus, @NonNull String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApplicationException(@NonNull HttpStatus httpStatus,
                                @NonNull String message,
                                @NonNull Throwable throwable) {
        super(message, throwable);
        this.httpStatus = httpStatus;
    }
}
