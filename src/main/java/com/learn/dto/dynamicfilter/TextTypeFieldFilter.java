package com.learn.dto.dynamicfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.dto.dynamicfilter.filtervaluetype.EqualityClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.NullClauseValue;
import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@Setter
@Getter
public class TextTypeFieldFilter extends AbstractFieldFilter {
    private static final Map<Boolean, String> LIKE_OR_ILIKE_MAP = Map.of(
            true, "LIKE",
            false, "ILIKE"
    );

    private enum Operation {
        // Query directly JSON Column in Postgres
        // https://popsql.com/learn-sql/postgresql/how-to-query-a-json-column-in-postgresql

        // The query will become ::
        // SELECT ...
        //FROM ...
        //WHERE student_name LIKE '%' || 'Shyam' || '%';
        EQUALS(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' = ?" + PARAMETER_INDEX_INDICATOR, true
        ),
        NOT_EQUALS(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' != ?" + PARAMETER_INDEX_INDICATOR, true
        ),
        CONTAINS(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' %likeOrILike '%' || ?" + PARAMETER_INDEX_INDICATOR + " || '%'", true
        ),
        DOES_NOT_CONTAINS(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' NOT %likeOrILike '%' || ?" + PARAMETER_INDEX_INDICATOR + " || '%'", true
        ),
        STARTS_WITH(
                COLUMN_NAME_INDICATOR +  " ->> '%fieldName' %likeOrILike ?" + PARAMETER_INDEX_INDICATOR + " || '%'", true
        ),
        DOES_NOT_STARTS_WITH(
                COLUMN_NAME_INDICATOR +  " ->> '%fieldName' NOT %likeOrILike ?" + PARAMETER_INDEX_INDICATOR + " || '%'", true
        ),
        ENDS_WITH(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' %likeOrILike '%' || ?" + PARAMETER_INDEX_INDICATOR, true
        ),
        DOES_NOT_ENDS_WITH(
                COLUMN_NAME_INDICATOR + " ->> '%fieldName' NOT %likeOrILike '%' || ?" + PARAMETER_INDEX_INDICATOR, true
        ),
        IS_BLANK(
                "(" + COLUMN_NAME_INDICATOR + " \\?\\? '%fieldName' AND " + COLUMN_NAME_INDICATOR + " ->> '%fieldName' IS NULL " + " )", false
        ),
        IS_NOT_BLANK(
                COLUMN_NAME_INDICATOR +  " ->> '%fieldName' IS NOT NULL", false
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

    private boolean caseSensitive = true;

    @JsonIgnore
    private boolean filterable = true;

    @JsonIgnore
    private String reasonForNotApplying;

    @Override
    String resolveWhereClause() {
        return operation.operatorValue.replace("%fieldName", fieldName);
    }

    @Override
    String resolveWhereClause(int parameterIndex) {
        return operation.operatorValue
                .replace("%fieldName", fieldName)
                .replace("%likeOrILike", LIKE_OR_ILIKE_MAP.get(caseSensitive))
                .replace(PARAMETER_INDEX_INDICATOR, String.valueOf(parameterIndex));
    }

    @Override
    public boolean isFilterable() {
        if (operation.requiredValueToApply) {
            filterable = StringUtils.isNotBlank(filterValue);
        }
        setReasonForNotApplying();
        return filterable;
    }

    @Override
    public WhereClauseValue getFilterValue() {
        return operation.requiredValueToApply ? new EqualityClauseValue(filterValue) : new NullClauseValue();
    }

    private void setReasonForNotApplying() {
        if (BooleanUtils.isTrue(filterable)) {
            return;
        }
        if (BooleanUtils.isNotTrue(log.isDebugEnabled())) {
            return;
        }
        reasonForNotApplying = "filterValue Not Found:: " + filterValue;
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
