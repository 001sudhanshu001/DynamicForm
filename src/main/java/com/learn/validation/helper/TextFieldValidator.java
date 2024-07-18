package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.learn.constants.FormFieldValidationRule.*;

public class TextFieldValidator implements FieldValidator{

    // These are the Rules which are applicable for TEXT InputType
    private static final Set<FormFieldValidationRule> applicableRules =
            Set.of(REQUIRED, MIN_LENGTH, MAX_LENGTH, PATTERN);

    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    private Pair<Boolean, Integer> minimumLengthValidationPair = Pair.of(Boolean.FALSE, null);
    private Pair<Boolean, Integer> maximumLengthValidationPair = Pair.of(Boolean.FALSE, null);

    public TextFieldValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
        this.payload = payload;
        this.errors = errors;
    }

    @Override
    public void validate() {
        validateRequiredRuleValidation();

        //validate if the ValidationRules are applicable to TEXT InputType
        validateApplicableRules();

        checkIfMinLengthIsLessThanMaxLength();
    }

    void validateRequiredRuleValidation() {
        FormFieldValidationRule required = FormFieldValidationRule.REQUIRED;
        String ruleValue = payload.getValidationRules().get(required);
        if (StringUtils.isBlank(ruleValue)) {
            errors.rejectValue(
                    "validationRules[" + required + "]",
                    "It is Mandatory To Tell That Field Is Required Or Not"
            );
            return;
        }

        CharSequence[] valuesThatWeCanConsiderAsBoolean = {"y", "n", "0", "1", "on", "off", "true", "false"};
        if (!StringUtils.equalsAnyIgnoreCase(ruleValue, valuesThatWeCanConsiderAsBoolean)) {
            errors.rejectValue(
                    "validationRules["+ FormFieldValidationRule.REQUIRED.name()+"]",
                    "Invalid Value Provided For " + FormFieldValidationRule.REQUIRED
            );
        }
    }

    private void validateApplicableRules() {
        Map<FormFieldValidationRule, String> validationRules = payload.getValidationRules();
        var ruleEntries = validationRules.entrySet();

        for (var ruleEntry: ruleEntries) {
            FormFieldValidationRule rule = ruleEntry.getKey();
            String ruleValue = ruleEntry.getValue();

            if (!applicableRules.contains(rule)) { // Applied a Rule which is not applicable for TEXT InputType
                errors.rejectValue(
                        "validationRules[" + rule.name() + "]",
                        "Rule Not Applicable On TEXT Type FormField"
                );
                continue;
            }

            if (MIN_LENGTH.equals(rule)) {
                checkMinLengthValidationValue(errors, ruleValue);
            } else if (MAX_LENGTH.equals(rule)) {
                checkMaxLengthValidationValue(errors, ruleValue);
            } else if (PATTERN.equals(rule)) {
                checkPatternValidationValue(errors, ruleValue);
            }
        }
    }

    private void checkMinLengthValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }

        try {
            int parsedInt = Integer.parseInt(ruleValue);
            if (parsedInt < 1) {
                errors.rejectValue(
                        "validationRules[MIN_LENGTH]",
                        "Required Positive Non-Zero Value For Rule :: MIN_LENGTH"
                );
            } else {
                // Value in correct
                minimumLengthValidationPair = Pair.of(Boolean.TRUE, parsedInt);
            }
        } catch (NumberFormatException exception) {
            errors.rejectValue(
                    "validationRules[MIN_LENGTH]",
                    "Invalid Value Provided For MIN_LENGTH"
            );
        }
    }

    private void checkMaxLengthValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }

        try {
            int parsedInt = Integer.parseInt(ruleValue);
            if (parsedInt < 1) {
                errors.rejectValue(
                        "validationRules[MAX_LENGTH]",
                        "Required Positive Non-Zero Value For Rule:: MAX_LENGTH"
                );
            } else {
                // Value in correct
                maximumLengthValidationPair = Pair.of(Boolean.TRUE, parsedInt);
            }
        } catch (NumberFormatException exception) {
            errors.rejectValue(
                    "validationRules[MAX_LENGTH]",
                    "Invalid Value Provided For MAX_LENGTH"
            );
        }
    }

    private void checkPatternValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            Pattern.compile(ruleValue);
        } catch (PatternSyntaxException exception) {
            errors.rejectValue(
                    "validationRules[PATTERN]",
                    "Invalid Value Provided For PATTERN"
            );
        }
    }

    private void checkIfMinLengthIsLessThanMaxLength() {
        Boolean canCheckMinimumLength = minimumLengthValidationPair.getKey();
        Boolean canCheckMaximumLength = maximumLengthValidationPair.getKey();

        if (canCheckMinimumLength && canCheckMaximumLength) {
            Integer minLen = minimumLengthValidationPair.getValue();
            Integer maxLen = maximumLengthValidationPair.getValue();

            if (minLen > maxLen) {
                String errorMessage = "%d MinLength Can Not Be Greater Than %d MaxLength".formatted(
                        minLen, maxLen
                );
                errors.rejectValue("validationRules", errorMessage);
            }
        }
    }

}
