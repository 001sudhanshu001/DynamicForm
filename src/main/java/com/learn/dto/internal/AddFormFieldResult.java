package com.learn.dto.internal;


import lombok.Getter;

@Getter
public class AddFormFieldResult {
    private boolean success;
    private String failMessage;

    private AddFormFieldResult() {}

    public static AddFormFieldResult successResult() {
        AddFormFieldResult addFormFieldResult = new AddFormFieldResult();
        addFormFieldResult.success = true;
        return addFormFieldResult;
    }

    public static AddFormFieldResult failResult(String withMessage) {
        AddFormFieldResult addFormFieldResult = new AddFormFieldResult();
        addFormFieldResult.success = false;
        addFormFieldResult.failMessage = withMessage;
        return addFormFieldResult;
    }
}
