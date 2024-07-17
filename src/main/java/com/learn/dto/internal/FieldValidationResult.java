package com.learn.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class FieldValidationResult {

    @JsonIgnore
    private boolean success;
    private String fieldName;
    private String failMessage;

}
