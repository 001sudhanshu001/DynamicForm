package com.learn.entity.validator;

import com.learn.dto.internal.FieldValidationResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpValueValidator implements FieldValueValidator{
    @Override
    public FieldValidationResult validate() {
        log.warn("This is NoOpValueValidator, No Field Type Matched, Use A Field Specific Validator");
        return FieldValidationResult.builder().success(true).build();
    }
}
