package com.learn.constants;

public enum FormFieldStatus {
    ACTIVE,
    IN_ACTIVE,
    DISABLE_FOR_SUBMISSION, // don't accept it in payload
    DISABLE_FOR_FILTER, // don't use this for dynamic filter
    DISABLE_FOR_RESPONSE, // don't send this input field data on frontend
}
