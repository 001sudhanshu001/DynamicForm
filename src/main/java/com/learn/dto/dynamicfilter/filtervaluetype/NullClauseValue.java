package com.learn.dto.dynamicfilter.filtervaluetype;

import lombok.ToString;

@ToString
public class NullClauseValue implements WhereClauseValue {
    @Override
    public Object value() {
        // will be used when filter applied like 'value is null' or 'value is not null'
        return null;
    }
}
