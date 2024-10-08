package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class DateValueValidator implements FieldValueValidator {

    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final String formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    @Override
    public FieldValidationResult validate() {
        validateAgainstRequiredRule();

        if (tryNextValidation) {
            validateProvidedValueIsParsableDate();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMinDateRule();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMaxDateRule();
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
                    .failMessage("DateTime Value Required But Not Found")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateProvidedValueIsParsableDate() {
        if (StringUtils.isBlank(formFieldValue)) {
            return;
        }
        try {
            LocalDateTime.parse(formFieldValue);
        } catch (DateTimeParseException parseException) {
            String failMessage = "Provided Value [%s] Is Not A Valid Date".formatted(formFieldValue);
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMinDateRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MIN_DATE);
        if (Objects.isNull(formFieldValue)) {
            return;
        }
        if (StringUtils.isBlank(validationValue)) {
            // no min date validation rule provided on creation
            return;
        }
        LocalDate minDateShouldBe = LocalDate.parse(validationValue);
        LocalDate actualDateProvided = LocalDate.parse(formFieldValue);
        if (actualDateProvided.isBefore(minDateShouldBe)) {
            String failMessage = "%s Field Required Minimum Date %s But Found %s".formatted(
                    fieldName, minDateShouldBe, actualDateProvided
            );
            result = FieldValidationResult.builder().success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMaxDateRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MAX_DATE);
        if (Objects.isNull(formFieldValue)) {
            return;
        }
        if (StringUtils.isBlank(validationValue)) {
            // no max date validation rule provided on creation
            return;
        }
        LocalDate maxDateShouldBe = LocalDate.parse(validationValue);
        LocalDate actualDateProvided = LocalDate.parse(formFieldValue);
        if (actualDateProvided.isAfter(maxDateShouldBe)) {
            String failMessage = "%s Field Required Maximum Date %s But Found %s".formatted(
                    fieldName, maxDateShouldBe, actualDateProvided
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
