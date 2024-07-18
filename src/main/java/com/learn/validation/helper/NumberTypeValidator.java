package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.learn.constants.FormFieldValidationRule.*;

public class NumberTypeValidator implements FieldValidator{

    private static final Set<FormFieldValidationRule> applicableRules =
            Set.of(REQUIRED, MIN_VALUE, MAX_VALUE);

    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    private Pair<Boolean, BigDecimal> minimumValueValidationPair = Pair.of(Boolean.FALSE, null);
    private Pair<Boolean, BigDecimal> maximumValueValidationPair = Pair.of(Boolean.FALSE, null);

    public NumberTypeValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
        this.payload = payload;
        this.errors = errors;
    }

    @Override
    public void validate() {
        //  It is Mandatory To Tell That Field Is Required Or Not
        RequiredValidationChecker.validateRequiredRuleValidation(payload, errors);
        validateApplicableRules();
        checkIfMinValueIsLessThanMaxValue();
    }

    private void validateApplicableRules() {
        Map<FormFieldValidationRule, String> validationRules = payload.getValidationRules();
        var ruleEntries = validationRules.entrySet();
        for (var ruleEntry: ruleEntries) {
            FormFieldValidationRule rule = ruleEntry.getKey();
            String ruleValue = ruleEntry.getValue();
            if (!applicableRules.contains(rule)) {
                errors.rejectValue(
                        "validationRules[" + rule.name() + "]",
                        "Rule Not Applicable On TEXT Type FormField"
                );
            }

            if (MIN_VALUE.equals(rule)) {
                checkMinNumberValidationValue(errors, ruleValue);
            } else if (MAX_VALUE.equals(rule)) {
                checkMaxNumberValidationValue(errors, ruleValue);
            }
        }
    }

    private void checkMinNumberValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isNotBlank(ruleValue)) {
            boolean parsable = NumberUtils.isParsable(ruleValue);
            if (BooleanUtils.isNotTrue(parsable)) {
                errors.rejectValue(
                        "validationRules[MIN_VALUE]",
                        "Invalid Value Provided For MIN_VALUE"
                );
            } else {
                BigDecimal minimumValue = NumberUtils.createBigDecimal(ruleValue);
                minimumValueValidationPair = Pair.of(Boolean.TRUE, minimumValue);
            }
        }
    }

    private void checkMaxNumberValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isNotBlank(ruleValue)) {
            boolean parsable = NumberUtils.isParsable(ruleValue);
            if (BooleanUtils.isNotTrue(parsable)) {
                errors.rejectValue(
                        "validationRules[MAX_VALUE]",
                        "Invalid Value Provided For MAX_VALUE"
                );
            } else {
                BigDecimal maxValue = NumberUtils.createBigDecimal(ruleValue);
                maximumValueValidationPair = Pair.of(Boolean.TRUE, maxValue);
            }
        }
    }


    private void checkIfMinValueIsLessThanMaxValue() {
        Boolean canCheckMinimumVal = minimumValueValidationPair.getKey();
        Boolean canCheckMaximumVal = maximumValueValidationPair.getKey();

        if (canCheckMinimumVal && canCheckMaximumVal) {
            BigDecimal minVal = minimumValueValidationPair.getValue();
            BigDecimal maxVal = maximumValueValidationPair.getValue();

            if (minVal.compareTo(maxVal) > 0) {
                String errorMessage = "%s MinValue Can Not Be Greater Than %s MaxValue".formatted(
                        minVal, maxVal
                );
                errors.rejectValue("validationRules", errorMessage);
            }
        }
    }


}
