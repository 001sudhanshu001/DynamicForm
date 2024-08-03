package com.learn.dto.dynamicfilter.filtervaluetype;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

@ToString
@RequiredArgsConstructor
public class RangeClauseValue implements WhereClauseValue {
    private final Object value1;
    private final Object value2;

    @Override
    public Pair<Object, Object> value() {
        return Pair.of(value1, value2);
    }

}
