package com.learn.dto.request;


import com.learn.constants.FormFieldValidationRule;
import com.learn.constants.InputType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HtmlFormFieldCreationPayload {

    @NotNull(message = "On Creation FormId Is Mandatory")
    @Min(value = 1, message = "Invalid Form Id Provided")
    private Long formId;

    @NotBlank
    @Size(min = 1, max = 100, message = "Field Name Should Between {0} And {1} Characters")
    private String name;

    @NotNull
    private InputType type;

    @NotBlank
    @Size(min = 1, max = 500, message = "Field Label Should Between {0} And {1} Characters")
    private String label;

    @NotNull
    @Size(min = 1)
    private Map<FormFieldValidationRule, String> validationRules;

    @NotBlank
    private String remarks;

    private String placeHolder;

    private String helpDescription;
}
