package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.PatternMatchUtils;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class TextTypeValueValidator implements FieldValueValidator{
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final String formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    @Override
    public FieldValidationResult validate() {
        validateAgainstRequiredRule();

        if (tryNextValidation) {
            validateAgainstMinLengthRule();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMaxValueRule();
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstPatternRule();
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
                    .failMessage("Field Value Required But Not Found")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMinLengthRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MIN_LENGTH);
        if (Objects.isNull(formFieldValue)) {
            return;
        }
        if (!NumberUtils.isParsable(validationValue)) {
            return;
        }

        int minLengthShouldBe = Integer.parseInt(validationValue); // if the Length is not specified in Number then return
        int actualLength = formFieldValue.length();

        if (actualLength < minLengthShouldBe) {
            String failMessage = "%s Field Required Minimum Value Of Length %d But Found %d".formatted(
                    fieldName, minLengthShouldBe, actualLength
            );
            result = FieldValidationResult.builder().success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMaxValueRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MAX_LENGTH);
        if (Objects.isNull(formFieldValue)) {
            return;
        }

        if (!NumberUtils.isParsable(validationValue)) { // if the Length is not specified in Number then return
            return;
        }

        int maxLengthShouldBe = Integer.parseInt(validationValue); // Because the max size of text is also specified in String
        int actualLength = formFieldValue.length();

        if (actualLength > maxLengthShouldBe) {
            String failMessage = "%s Field Required Maximum Value Of Length %d But Found %d".formatted(
                    fieldName, maxLengthShouldBe, actualLength
            );
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstPatternRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.PATTERN);
        if (validationValue == null || StringUtils.isBlank(validationValue)) {
            return;
        }
        boolean simpleMatch = PatternMatchUtils.simpleMatch(validationValue, formFieldValue);
        if (BooleanUtils.isNotTrue(simpleMatch)) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("Value Does Not Satisfy Validation Pattern:: " + validationValue)
                    .build();
            tryNextValidation = false;
        }
    }
}
