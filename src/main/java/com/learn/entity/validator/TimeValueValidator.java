package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class TimeValueValidator implements FieldValueValidator{
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final String formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    @Override
    public FieldValidationResult validate() {
        validateAgainstRequiredRule();

        if (tryNextValidation) {
            validateProvidedValueIsParsableTime();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMinTimeRule();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMaxTimeRule();
        } else {
            return result;
        }

        return result;
    }

    private void validateAgainstRequiredRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.REQUIRED);
        if (BooleanUtils.toBoolean(validationValue) && StringUtils.isBlank(formFieldValue)) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("Date Value Required But Not Found")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateProvidedValueIsParsableTime() {
        if (StringUtils.isBlank(formFieldValue)) {
            return;
        }
        try {
            LocalTime.parse(formFieldValue);
        } catch (DateTimeParseException parseException) {
            String failMessage = "Provided Value [%s] Is Not A Valid Time".formatted(formFieldValue);
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMinTimeRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MIN_TIME);
        if (Objects.isNull(formFieldValue)) {
            return;
        }

        if (StringUtils.isBlank(validationValue)) {
            // no min time validation rule provided on creation
            return;
        }

        LocalTime minTimeShouldBe = LocalTime.parse(validationValue);
        LocalTime actualTimeProvided = LocalTime.parse(formFieldValue);
        if (actualTimeProvided.isBefore(minTimeShouldBe)) {
            String failMessage = "%s Field Required Minimum Time %s But Found %s".formatted(
                    fieldName, minTimeShouldBe, actualTimeProvided
            );
            result = FieldValidationResult.builder().success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMaxTimeRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MAX_TIME);
        if (Objects.isNull(formFieldValue)) {
            return;
        }
        if (StringUtils.isBlank(validationValue)) {
            // no max time validation rule provided on creation
            return;
        }
        LocalTime maxTimeShouldBe = LocalTime.parse(validationValue);
        LocalTime actualTimeProvided = LocalTime.parse(formFieldValue);
        if (actualTimeProvided.isAfter(maxTimeShouldBe)) {
            String failMessage = "%s Field Required Maximum Time %s But Found %s".formatted(
                    fieldName, maxTimeShouldBe, actualTimeProvided
            );
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

}
