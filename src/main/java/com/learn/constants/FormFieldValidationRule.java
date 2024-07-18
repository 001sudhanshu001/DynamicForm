package com.learn.constants;

public enum FormFieldValidationRule {
    REQUIRED,
    MIN_LENGTH,
    MAX_LENGTH,
    MIN_VALUE,
    MAX_VALUE,
    PATTERN,

    MIN_DATE, MAX_DATE, // for date fields
    MIN_TIME, MAX_TIME // for time fields
}