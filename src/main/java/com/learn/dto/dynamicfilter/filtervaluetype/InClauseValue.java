package com.learn.dto.dynamicfilter.filtervaluetype;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class InClauseValue implements WhereClauseValue {
    private final Object inClause;

    @Override
    public Object value() {
        return inClause;
    }

}
