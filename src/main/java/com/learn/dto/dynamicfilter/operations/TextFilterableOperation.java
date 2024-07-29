package com.learn.dto.dynamicfilter.operations;

import lombok.Getter;

@Getter
public enum TextFilterableOperation {
    EQUALS(true),
    NOT_EQUALS(true),
    CONTAINS(true),
    DOES_NOT_CONTAINS(true),
    STARTS_WITH(true),
    DOES_NOT_STARTS_WITH(true),
    ENDS_WITH(true),
    DOES_NOT_ENDS_WITH(true),
    IS_BLANK(false),
    IS_NOT_BLANK(false);

    private final boolean requiredValueToApply;

    TextFilterableOperation(boolean requiredValueToApply) {
        this.requiredValueToApply = requiredValueToApply;
    }

}
