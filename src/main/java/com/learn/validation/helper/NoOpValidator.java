package com.learn.validation.helper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpValidator implements FieldValidator {
    @Override
    public void validate() {
        log.warn("NoOp Validator Called, Use A Field Specific One");
    }

}
