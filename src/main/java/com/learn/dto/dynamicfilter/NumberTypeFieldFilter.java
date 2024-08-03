package com.learn.dto.dynamicfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.dto.dynamicfilter.filtervaluetype.EqualityClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.NullClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.RangeClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class NumberTypeFieldFilter extends AbstractFieldFilter {

    private enum Operation {
        EQUALS(
                "CAST(" + COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) = ?" + PARAMETER_INDEX_INDICATOR, 1
        ),
        NOT_EQUALS(
                "CAST(" + COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) != ?" + PARAMETER_INDEX_INDICATOR, 1
        ),
        GREATER_THAN(
                "CAST(" +  COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) > ?" + PARAMETER_INDEX_INDICATOR, 1
        ),
        GREATER_THAN_OR_EQUALS(
                "CAST(" +  COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) >= ?" + PARAMETER_INDEX_INDICATOR, 1
        ),
        LESS_THAN(
                "CAST(" + COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) < ?" + PARAMETER_INDEX_INDICATOR, 1
        ),
        LESS_THAN_OR_EQUALS(
                "CAST(" + COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) <= ?" + PARAMETER_INDEX_INDICATOR, 1
        ),
        BETWEEN(
                "CAST(" + COLUMN_NAME_INDICATOR + " ->> '%fieldName' AS DECIMAL) BETWEEN ?" + PARAMETER_INDEX_INDICATOR + " AND ?" + PARAMETER_INDEX_INDICATOR, 2
        ),
        IS_NULL(
                "( " + COLUMN_NAME_INDICATOR + " \\?\\? '%fieldName' AND " + COLUMN_NAME_INDICATOR + " ->> '%fieldName' IS NULL " + ")", 0
        ),
        IS_NOT_NULL(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' IS NOT NULL", 0
        );

        private final String operatorValue;
        private final int numberOfValuesRequired;

        Operation(String operatorValue, int numberOfValuesRequired) {
            this.operatorValue = operatorValue;
            this.numberOfValuesRequired = numberOfValuesRequired;
        }
    }

    @NotBlank
    private String fieldName;

    @NotNull
    private Operation operation;

    @Size(min = 1, max = 2)
    private List<? extends Number> filterValue;

    @JsonIgnore
    private boolean filterable;

    @JsonIgnore
    private String reasonForNotApplying;

    @Override
    public boolean isFilterable() {
        if (operation.equals(Operation.BETWEEN)) {
            filterableWhenBetweenApplied();
        } else {
            filterableWhenOtherThanBetweenApplied();
        }

        return filterable;
    }

    private void filterableWhenBetweenApplied() {
        if (filterValue.size() != REQUIRED_FILTER_VALUES_COUNT_FOR_BETWEEN)  {
            filterable = false;
            setReasonAsCountMisMatchForBetween();
        } else {
            long nonNullCount = filterValue.stream().filter(Objects::nonNull).count();
            filterable = nonNullCount == REQUIRED_FILTER_VALUES_COUNT_FOR_BETWEEN;
            setReasonAsNonNullValueRequired();
        }
    }

    private void setReasonAsCountMisMatchForBetween() {
        reasonForNotApplying =
                "When Between Applied For Number, Required 2 FilterValues, But Found :: " + filterValue.size();
    }

    private void setReasonAsNonNullValueRequired() {
        if (BooleanUtils.isTrue(filterable)) {
            return;
        }
        reasonForNotApplying = "When Between Applied For Number, Both FilterValues Should Be Non-Null";
    }

    private void filterableWhenOtherThanBetweenApplied() {
        filterable = operation.numberOfValuesRequired == filterValue.size();
        setReasonAsCountMisMatch();
    }

    private void setReasonAsCountMisMatch() {
        if (BooleanUtils.isTrue(filterable)) {
            return;
        }

        String failReasonTemplate = "Number Of FilterValues Required For Operation %s is %d But Found:: %d";
        reasonForNotApplying = failReasonTemplate.formatted(
                operation, operation.numberOfValuesRequired, filterValue.size()
        );
    }

    @Override
    public String resolveWhereClause() {
        return operation.operatorValue.replace("%fieldName", fieldName);
    }

    @Override
    public String resolveWhereClause(int parameterIndex) {
        return operation.operatorValue
                .replace("%fieldName", fieldName)
                .replaceFirst(PARAMETER_INDEX_INDICATOR, String.valueOf(parameterIndex));
    }

    @Override
    public WhereClauseValue getFilterValue() {
        WhereClauseValue whereClauseValue;
        if (operation.numberOfValuesRequired == 0) {
            whereClauseValue = new NullClauseValue();
        } else if (operation.numberOfValuesRequired == 1) {
            whereClauseValue = new EqualityClauseValue(filterValue.getFirst());
        } else {
            whereClauseValue = new RangeClauseValue(filterValue.getFirst(), filterValue.getLast());
        }
        return whereClauseValue;
    }

    @Override
    public String toString() {
        return "NumberTypeFieldFilter{" +
                "fieldName='" + fieldName + '\'' +
                ", operation=" + operation +
                ", filterValue=" + filterValue +
                ", filterable=" + filterable +
                ", reasonForNotApplying='" + reasonForNotApplying + '\'' +
                '}';
    }

}
