package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class NumberTypeValueValidator {
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final Object formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    public FieldValidationResult validate() {
        if (formFieldValue == null) {
            return validateValueAsNumber(null);
        } else {
            String numberAsString = Objects.toString(formFieldValue);
            boolean parsable = NumberUtils.isParsable(numberAsString);
            if (!parsable) {
                return FieldValidationResult.builder()
                        .success(false)
                        .fieldName(fieldName)
                        .failMessage("Provided Value Is Not Number:: [" + numberAsString + "]")
                        .build();
            } else {
                BigDecimal bigDecimal = NumberUtils.createBigDecimal(numberAsString);
                return validateValueAsNumber(bigDecimal);
            }
        }
    }


    private FieldValidationResult validateValueAsNumber(BigDecimal numberValue) {
        validateAgainstRequiredRule(numberValue);

        if (tryNextValidation) {
            validateAgainstMinValueRule(numberValue);
        } else {
            return result;
        }

        if (tryNextValidation) {
            validateAgainstMaxValueRule(numberValue);
        } else {
            return result;
        }

        return result;
    }

    private void validateAgainstRequiredRule(BigDecimal numberValue) {
        String validationValue = validationRules.get(FormFieldValidationRule.REQUIRED);
        if (!BooleanUtils.toBoolean(validationValue)) {
            // value not required
            return;
        }

        if (Objects.isNull(numberValue)) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("Field Value Required But Not Found")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMinValueRule(BigDecimal numberValue) {
        String validationValue = validationRules.get(FormFieldValidationRule.MIN_VALUE);
        if (StringUtils.isBlank(validationValue)) {
            // Don't apply rule if there is no value
            return;
        }

        BigDecimal minimumValueShouldBe = NumberUtils.createBigDecimal(validationValue);
        if (Objects.isNull(numberValue)) {
            // Don't apply Rule, if value is null then should catch in Required Rule
            return;
        }

        if (minimumValueShouldBe.compareTo(numberValue) > 0) {
            String failMessage = "%s Field Required Minimum Value %s But Found %s".formatted(
                    fieldName, minimumValueShouldBe.doubleValue(), numberValue.doubleValue()
            );
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMaxValueRule(BigDecimal numberValue)  {
        String validationValue = validationRules.get(FormFieldValidationRule.MAX_VALUE);
        if (StringUtils.isBlank(validationValue)) {
            // Don't apply rule if there is no value
            return;
        }

        BigDecimal maximumValueShouldBe = NumberUtils.createBigDecimal(validationValue);
        if (Objects.isNull(numberValue)) {
            // Don't apply Rule, if value is null then should catch in Required Rule
            return;
        }

        if (maximumValueShouldBe.compareTo(numberValue) < 0) {
            String failMessage = "%s Field Required Maximum Value %s But Found %s".formatted(
                    fieldName, maximumValueShouldBe.doubleValue(), numberValue.doubleValue()
            );

            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage(failMessage)
                    .build();
        }
    }
}
