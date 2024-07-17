package com.learn.dto.internal;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FieldStatusChangeResult {
    private final boolean success;
    private HttpStatus httpStatus;
    private String failMessage;

    private FieldStatusChangeResult(boolean success) {
        this.success = success;
    }

    public static FieldStatusChangeResult successResult() {
        return new FieldStatusChangeResult(true);
    }

    public static FieldStatusChangeResult failResult(HttpStatus withStatus, String withMessage) {
        FieldStatusChangeResult result = new FieldStatusChangeResult(false);
        result.httpStatus = withStatus;
        result.failMessage = withMessage;
        return result;
    }

}
