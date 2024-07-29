package com.learn.dto.dynamicfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.dto.dynamicfilter.filtervaluetype.EqualityClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.NullClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;
import com.learn.dto.dynamicfilter.operations.TextFilterableOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.StringJoiner;

@Slf4j
@Setter
@Getter
public class TextTypeFieldFilter extends AbstractFieldFilter {

    @NotBlank
    private String fieldName;

    @NotNull
    private TextFilterableOperation operation;

    private String filterValue;

    private boolean caseSensitive = true;

    @JsonIgnore
    private boolean filterable = true;

    @Override
    public boolean isFilterable() {
        if (operation.isRequiredValueToApply()) {
            filterable = StringUtils.isNotBlank(filterValue);
        }

        if (log.isDebugEnabled()) {
            log.debug("{}, isFilterable: {}", this, filterable);
        }

        return filterable;
    }

    @Override
    public WhereClauseValue getFilterValue() {
        WhereClauseValue whereClauseValue =
                operation.isRequiredValueToApply() ? new EqualityClauseValue(filterValue)
                        : new NullClauseValue();

        if (log.isDebugEnabled()) {
            log.debug("{}, getFilterValue: {}", this, whereClauseValue);
        }
        return whereClauseValue;

    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TextTypeFieldFilter.class.getSimpleName() + "[", "]")
                .add("fieldName='" + fieldName + "'")
                .add("operation=" + operation)
                .add("filterValue='" + filterValue + "'")
                .add("caseSensitive=" + caseSensitive)
                .add("filterable=" + filterable)
                .toString();
    }

}
