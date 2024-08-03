package com.learn.dto.dynamicfilter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.learn.dto.dynamicfilter.filtervaluetype.WhereClauseValue;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "fieldType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "TEXT", value = TextTypeFieldFilter.class),
        @JsonSubTypes.Type(name = "NUMBER", value = NumberTypeFieldFilter.class),
        @JsonSubTypes.Type(name = "RADIO", value = RadioTypeFieldFilter.class),

})
public abstract class AbstractFieldFilter {
    public static final String PARAMETER_INDEX_INDICATOR = "_________index_________";
    public static final String COLUMN_NAME_INDICATOR = "_________columnName_________";
    public static final int REQUIRED_FILTER_VALUES_COUNT_FOR_BETWEEN = 2;

    public abstract boolean isFilterable();
    public abstract WhereClauseValue getFilterValue();
    public abstract String getFieldName();
    protected abstract String resolveWhereClause();
    public abstract String resolveWhereClause(int parameterIndex);

    public abstract Object getOperation();


    // Only for Number, Date, Time and DateTime fields
    protected String resolveForBetweenCase(int firstIndex, int secondIndex) {
        throw new UnsupportedOperationException("This method is not supported for this class");
    }
}
