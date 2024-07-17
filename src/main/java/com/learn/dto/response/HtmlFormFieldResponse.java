package com.learn.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormFieldValidationRule;
import com.learn.constants.InputType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class HtmlFormFieldResponse {
    private Long id;
    private String name;
    private InputType type;
    private String label;
    private Map<FormFieldValidationRule, String> validationRules;
    private Integer sortingOrder;
    private FormFieldStatus formFieldStatus;
    private String remarks;
    private String placeHolder;
    private String helpDescription;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long version;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> displayOptions; // This is for Radio

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlFormFieldResponse that = (HtmlFormFieldResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
