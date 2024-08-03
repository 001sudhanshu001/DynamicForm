package com.learn.dto.dynamicfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.dto.dynamicfilter.filtervaluetype.EqualityClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.InClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.RangeClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AppliedDynamicFilter {

    @NotNull(message = "fieldFilters cannot be null")
    @Size(min = 1, message = "fieldFilters must contains at least one filter")
    private List<AbstractFieldFilter> fieldFilters;

    @JsonIgnore
    private int index = 1;

    @JsonIgnore
    private List<String> whereClauses = new ArrayList<>();

    @JsonIgnore
    private List<Object> queryParameters = new ArrayList<>();

    public void resolveWhereClausesWithQueryParams() {
        for (AbstractFieldFilter filter : fieldFilters) {
            if(filter.isFilterable()) {
                setValueInWhereClausesAndQueryParameters(filter);
            }
        }
    }

    private void setValueInWhereClausesAndQueryParameters(AbstractFieldFilter filter) {
        WhereClauseValue whereClauseValue = filter.getFilterValue();

        if (whereClauseValue instanceof EqualityClauseValue) {
            EqualityClauseValue equalityClauseValue = (EqualityClauseValue) whereClauseValue;
            equalityCheck(filter, equalityClauseValue);
        } else if (whereClauseValue instanceof RangeClauseValue) {
            RangeClauseValue rangeClauseValue = (RangeClauseValue) whereClauseValue;
            rangeCheck(filter, rangeClauseValue);
        } else if (whereClauseValue instanceof InClauseValue) {
            InClauseValue inClauseValue = (InClauseValue) whereClauseValue;
            inCheck(filter, inClauseValue);
        } else {
            whereClauses.add(filter.resolveWhereClause());
        }

    }

    private void equalityCheck(AbstractFieldFilter filter, EqualityClauseValue equalityClauseValue) {
        whereClauses.add(filter.resolveWhereClause(index));
        queryParameters.add(equalityClauseValue.value());
        index++;
    }

    private void rangeCheck(AbstractFieldFilter filter, RangeClauseValue rangeClauseValue) {
        Pair<Object, Object> pair = rangeClauseValue.value();

        String resolveForBetweenCase = filter.resolveForBetweenCase(index, ++index);
        whereClauses.add(resolveForBetweenCase);
        queryParameters.add(pair.getLeft());
        queryParameters.add(pair.getRight());
    }

    private void inCheck(AbstractFieldFilter filter, InClauseValue inClauseValue) {
        Object value = inClauseValue.value();
        whereClauses.add(filter.resolveWhereClause(index));
        queryParameters.add(value);
        index++;
    }

}
