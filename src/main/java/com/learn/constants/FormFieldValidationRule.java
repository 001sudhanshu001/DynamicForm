package com.learn.constants;

public enum FormFieldValidationRule {
    REQUIRED, // for all fields
    MIN_LENGTH, MAX_LENGTH, PATTERN, // for text fields
    MIN_VALUE, MAX_VALUE, // for number fields
    MIN_DATE, MAX_DATE, // for date fields
    MIN_TIME, MAX_TIME, // for time fields
    MIN_DATE_TIME, MAX_DATE_TIME, // for datetime fields
    ALLOWED_FILE_TYPES, MIN_FILES, MAX_FILES, // for file fields
}