package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;

import static com.learn.constants.FormFieldValidationRule.REQUIRED;

public class CheckBoxTypeValidator implements FieldValidator {

    // The only Validation Rule for CheckBox is REQUIRED
    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    public CheckBoxTypeValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
        this.payload = payload;
        this.errors = errors;
    }

    @Override
    public void validate() {
        // It is Mandatory To Tell That Field Is Required Or Not
        RequiredValidationChecker.validateRequiredRuleValidation(payload, errors);

        validateCheckBoxTypeFormField();
    }

    private void validateCheckBoxTypeFormField() {
        validateIfProvidedValidationRulesAreCorrect();

        Map<String, String> displayOptions = payload.getDisplayOptions();
        if (displayOptions.isEmpty()) {
            errors.rejectValue("displayOptions", "DisplayOptions Required For CheckBoxField");
        } else {
            validateIfDisplayOptionHasNonBlankPair(displayOptions);
            validateDisplayOptionsKeysAreUnique(displayOptions);
            // TODO : Similar to validateDisplayOptionsKeysAreUnique, validateDisplayOptionsValuesAreUnique
            //  can be implemented base on specific need
        }
    }

    private void validateIfProvidedValidationRulesAreCorrect() {
        Map<FormFieldValidationRule, String> validationRules = payload.getValidationRules();
        validationRules.forEach((validationRule, s) -> {
            if (!validationRule.equals(REQUIRED)) { // The only Validation Rule for CheckBox is REQUIRED
                errors.rejectValue(
                        "validationRules[" + validationRule.name() + "]",
                        "Rule Not Applicable On CheckBox"
                );
            }
        });
    }

    private void validateIfDisplayOptionHasNonBlankPair(Map<String, String> displayOptions) {
        displayOptions.forEach((optionKey, optionValue) -> {
            if (StringUtils.isBlank(optionKey)) {
                errors.rejectValue("displayOptions", "Key Can't Be Empty");
            }
            if (StringUtils.isBlank(optionValue)) {
                String errorMessage = "CheckBoxField DisplayOption %s Found With Empty DisplayValue"
                        .formatted(optionKey);

                errors.rejectValue("displayOptions", errorMessage);
            }
        });
    }

    private void validateDisplayOptionsKeysAreUnique(Map<String, String> displayOptions) {
        // Map to count occurrences of each display key
        Map<String, Integer> displayValuesCount = new HashMap<>();

        for (String key : displayOptions.keySet()) {
            if (StringUtils.isNotBlank(key)) {
                String trimmedKey = StringUtils.trim(key);

                displayValuesCount.put(trimmedKey, displayValuesCount.getOrDefault(trimmedKey, 0) + 1);
            }
        }

        // Check if there are duplicate display keys
        for (Map.Entry<String, Integer> entry : displayValuesCount.entrySet()) {
            if (entry.getValue() > 1) {
                errors.rejectValue(
                        "displayOptions",
                        "More Than One Similar DisplayOption Key Found ::" + entry.getKey()
                );
            }
        }
    }

}
