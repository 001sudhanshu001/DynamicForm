package com.learn.dto.dynamicfilter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppliedDynamicFilter {

    @NotNull(message = "fieldFilters cannot be null")
    @Size(min = 1, message = "fieldFilters must contains at least one filter")
    private List<AbstractFieldFilter> fieldFilters;

}