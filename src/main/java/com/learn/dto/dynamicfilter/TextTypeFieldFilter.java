package com.learn.dto.dynamicfilter;

import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
public final class TextTypeFieldFilter extends AbstractFieldFilter {
    @Override
    public boolean isFilterable() {
        return false;
    }

    @Override
    public WhereClauseValue getFilterValue() {
        return null;
    }

    @Override
    public String getFieldName() {
        return null;
    }

    @Override
    public Object getOperation() {
        return null;
    }
}
