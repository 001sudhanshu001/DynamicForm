package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RequiredArgsConstructor
public class DateValueValidator implements FieldValueValidator{
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final String formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    @Override
    public FieldValidationResult validate() {
        validateAgainstRequiredRule();

        if (tryNextValidation) {
            validateProvidedValueIsParsableDateTime();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMinDateTimeRule();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMaxDateTimeRule();
        } else {
            return result;
        }

        return result;
    }

    private void validateAgainstRequiredRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.REQUIRED);

        // Value is required but not Provided
        if (BooleanUtils.toBoolean(validationValue) && StringUtils.isBlank(formFieldValue)) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("DateTime Value Required But Not Found")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateProvidedValueIsParsableDateTime() {
        if (StringUtils.isBlank(formFieldValue)) {
            return;
        }
        try {
            LocalDateTime.parse(formFieldValue);
        } catch (DateTimeParseException parseException) {
            String failMessage = "Provided Value [%s] Is Not A Valid DateTime".formatted(formFieldValue);
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMinDateTimeRule() {
        if (StringUtils.isBlank(formFieldValue)) {
            return;
        }

        String minDateTimeRule =
                validationRules.get(FormFieldValidationRule.MIN_DATE_TIME);

        if (BooleanUtils.isNotTrue(isMinDateTimeValid(LocalDateTime.parse(formFieldValue), minDateTimeRule))) {
            String failMessage = "%s Field Required Minimum DateTime %s But Found %s".formatted(
                    fieldName, minDateTimeRule, formFieldValue
            );
            result = FieldValidationResult.builder().success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMaxDateTimeRule() {
        if (StringUtils.isBlank(formFieldValue)) {
            return;
        }
        String maxDateTimeRule = validationRules.get(FormFieldValidationRule.MAX_DATE_TIME);

        if (BooleanUtils.isNotTrue(isMaxDateTimeValid(LocalDateTime.parse(formFieldValue), maxDateTimeRule))) {
            String failMessage = "%s Field Required Maximum DateTime %s But Found %s".formatted(
                    fieldName, maxDateTimeRule, formFieldValue
            );
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    public boolean isMaxDateTimeValid(LocalDateTime providedDateTime, String maxDateTimeRule) {
        if (StringUtils.isBlank(maxDateTimeRule)) {
            return true;
        }

        LocalDateTime maxLocalDateTimeAllowed = LocalDateTime.parse(maxDateTimeRule);
        return providedDateTime.isBefore(maxLocalDateTimeAllowed) ||
               providedDateTime.isEqual(maxLocalDateTimeAllowed);

    }

    public boolean isMinDateTimeValid(LocalDateTime providedDateTime, String minDateTimeRule) {
        if (StringUtils.isBlank(minDateTimeRule)) {
            return true;
        }

        LocalDateTime minLocalDateTimeAllowed = LocalDateTime.parse(minDateTimeRule);
        return providedDateTime.isAfter(minLocalDateTimeAllowed) ||
                providedDateTime.isEqual(minLocalDateTimeAllowed);
    }

}
