package com.learn.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SubmitDynamicFormPayload {

    @NotNull
    @Min(1)
    private Long formId; // In case of Filled Form Update, this filed will contain the ID of FilledForm

    // TODO : Now it is not Required, Delete it after demo
//    @NotNull
//    @Min(1)
    private Long userId;

    @Size(min = 1)
    private Map<String, Object> fieldValues = new HashMap<>();

    @JsonAnySetter
    public void addFieldValue(String fieldName, Object fieldValue) {
        fieldValues.put(fieldName, fieldValue);
    }

}
