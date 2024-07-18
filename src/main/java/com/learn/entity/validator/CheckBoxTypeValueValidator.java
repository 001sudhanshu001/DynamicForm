package com.learn.entity.validator;


import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.util.*;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class CheckBoxTypeValueValidator {
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final Map<String, String> displayOptions;
    private final Object formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    public FieldValidationResult validate() {
        validateValueType();
        if (tryNextValidation) {
            validateAgainstRequiredRule();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateRightOptionProvidedOrNot();
        } else {
            return result;
        }
        return result;
    }

    private void validateValueType() {
        if (formFieldValue instanceof List<?> list) {
            Optional<?> invalidTypeListProvided = Optional.empty();
            for (Object element : list) {
                if (!(element instanceof String)) {
                    invalidTypeListProvided = Optional.of(element);
                    break;
                }
            }

            if (invalidTypeListProvided.isPresent()) {
                result = FieldValidationResult.builder()
                        .success(false)
                        .fieldName(fieldName)
                        .failMessage("CheckBox Only Accept Input As String Array")
                        .build();
                tryNextValidation = false;
            }
            return;
        }

        result = FieldValidationResult.builder()
                .success(false)
                .fieldName(fieldName)
                .failMessage("CheckBox Only Accept Input As String Array")
                .build();
        tryNextValidation = false;
    }

    private void validateAgainstRequiredRule() {
        String requiredOrNot = validationRules.get(FormFieldValidationRule.REQUIRED);
        boolean required = BooleanUtils.toBoolean(requiredOrNot);

        if (BooleanUtils.isNotTrue(required)) {
            // do not check value if not required
            return;
        }

        List<String> providedOptions = formFieldValue != null ? (List<String>) formFieldValue : Collections.emptyList();
        if (required && providedOptions.isEmpty()) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("At Least One Checkbox Value Must Select")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateRightOptionProvidedOrNot() {
        Set<String> possibleSelections = displayOptions.keySet();

        List<String> providedOptions = formFieldValue != null ? (List<String>) formFieldValue : Collections.emptyList();

        List<String> invalidSelections = providedOptions.stream()
                .filter(Predicate.not(possibleSelections::contains))
                .toList();

        if (!invalidSelections.isEmpty()) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("Invalid CheckBox Options Provided:: " + invalidSelections)
                    .build();
            tryNextValidation = false;
        }
    }

}
