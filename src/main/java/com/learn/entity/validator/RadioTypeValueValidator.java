package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class RadioTypeValueValidator {
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final Map<String, String> displayOptions;
    private final Object formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    public FieldValidationResult validate() {
        validateAgainstRequiredRule();

        if (tryNextValidation) {
            validateProvidedValueIsValidOption();
        } else {
            return result;
        }
        return result;
    }

    private void validateAgainstRequiredRule() {
        String requiredOrNot = validationRules.get(FormFieldValidationRule.REQUIRED);
        boolean required = BooleanUtils.toBoolean(requiredOrNot);

        if (required && Objects.isNull(formFieldValue)) { // Field is required but value is not provided in the form
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("Field Value Required But Not Found")
                    .build();

            tryNextValidation = false;
        }
    }

    private void validateProvidedValueIsValidOption() {
        String providedOption = String.valueOf(formFieldValue);
        if (!displayOptions.containsKey(providedOption)) { // Provided option is not the display Options
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("Invalid Radio Option Provided:: " + providedOption)
                    .build();

            tryNextValidation = false;
        }
    }

}
