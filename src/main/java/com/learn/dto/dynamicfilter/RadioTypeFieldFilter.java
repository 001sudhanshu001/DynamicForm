package com.learn.dto.dynamicfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.dto.dynamicfilter.filtervaluetype.EqualityClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.NullClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class RadioTypeFieldFilter extends AbstractFieldFilter {

    private enum Operation {
        EQUALS(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' = ?" + PARAMETER_INDEX_INDICATOR, true
        ),
        NOT_EQUALS(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' != ?" + PARAMETER_INDEX_INDICATOR, true
        ),
        IS_BLANK(
                "(" + COLUMN_NAME_INDICATOR + " \\?\\? '%fieldName' AND " + COLUMN_NAME_INDICATOR + " ->> '%fieldName' IS NULL " + " )", false
        );

        private final String operatorValue;
        private final boolean requiredValueToApply;

        Operation(String operatorValue, boolean requiredValueToApply) {
            this.operatorValue = operatorValue;
            this.requiredValueToApply = requiredValueToApply;
        }
    }

    @NotBlank
    private String fieldName;

    @NotNull
    private Operation operation;

    private String filterValue;

    @JsonIgnore
    private boolean filterable = true;

    @JsonIgnore
    private String reasonForNotApplying;

    @Override
    public boolean isFilterable() {
        if (operation.requiredValueToApply) {
            filterable = StringUtils.isNotBlank(filterValue);
        }

        if(!filterable) {
            reasonForNotApplying = "filterValue Not Found:: " + filterValue;
        }
        return filterable;
    }


    @Override
    public WhereClauseValue getFilterValue() {
        return operation.requiredValueToApply ? new EqualityClauseValue(filterValue) : new NullClauseValue();
    }

    @Override
    public String resolveWhereClause() {
        return operation.operatorValue.replace("%fieldName", fieldName);
    }

    @Override
    public String resolveWhereClause(int parameterIndex) {
        return operation.operatorValue
                .replace("%fieldName", fieldName)
                .replace(PARAMETER_INDEX_INDICATOR, String.valueOf(parameterIndex));
    }

}
