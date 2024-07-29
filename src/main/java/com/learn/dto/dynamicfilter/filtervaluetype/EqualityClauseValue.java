package com.learn.dto.dynamicfilter.filtervaluetype;

import lombok.ToString;

@ToString
public class EqualityClauseValue implements WhereClauseValue{
    private final Object checkEquality;

    public EqualityClauseValue(Object checkEquality) {
        this.checkEquality = checkEquality;
    }

    @Override
    public Object value() {
        return checkEquality;
    }
}
