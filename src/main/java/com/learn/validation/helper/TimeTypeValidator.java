package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import static com.learn.constants.FormFieldValidationRule.*;

public class TimeTypeValidator implements FieldValidator{

    private static final Set<FormFieldValidationRule> applicableRules =
            Set.of(REQUIRED, MIN_TIME, MAX_TIME);

    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    private Pair<Boolean, LocalTime> minimumTimeValidationPair = Pair.of(Boolean.FALSE, null);
    private Pair<Boolean, LocalTime> maximumTimeValidationPair = Pair.of(Boolean.FALSE, null);

    public TimeTypeValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
        this.payload = payload;
        this.errors = errors;
    }

    @Override
    public void validate() {
        RequiredValidationChecker.validateRequiredRuleValidation(payload, errors);
        validateApplicableRules();
        checkIfMinTimeIsLessThanMaxTime();
    }

    private void validateApplicableRules() {
        Map<FormFieldValidationRule, String> validationRules = payload.getValidationRules();
        Set<Map.Entry<FormFieldValidationRule, String>> ruleEntries = validationRules.entrySet();
        for (Map.Entry<FormFieldValidationRule, String> ruleEntry: ruleEntries) {
            FormFieldValidationRule rule = ruleEntry.getKey();
            String ruleValue = ruleEntry.getValue();
            if (!applicableRules.contains(rule)) {
                errors.rejectValue(
                        "validationRules[" + rule.name() + "]",
                        "Rule Not Applicable On Date Type FormField"
                );
                continue;
            }
            if (MIN_TIME.equals(rule)) {
                checkMinTimeValidationValue(errors, ruleValue);
            } else if (MAX_TIME.equals(rule)) {
                checkMaxTimeValidationValue(errors, ruleValue);
            }
        }
    }

    private void checkMinTimeValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            LocalTime parsedTime = LocalTime.parse(ruleValue);
            minimumTimeValidationPair = Pair.of(Boolean.TRUE, parsedTime);
        } catch (DateTimeParseException exception) {
            errors.rejectValue(
                    "validationRules[" + MIN_TIME + "]",
                    "Invalid Value Provided For " + MIN_TIME + " => " + ruleValue
            );
        }
    }

    private void checkMaxTimeValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            LocalTime parsedTime = LocalTime.parse(ruleValue);
            maximumTimeValidationPair = Pair.of(Boolean.TRUE, parsedTime);
        } catch (DateTimeParseException exception) {
            errors.rejectValue(
                    "validationRules[" + MAX_TIME + "]",
                    "Invalid Value Provided For " + MAX_TIME + " => " + ruleValue
            );
        }
    }

    private void checkIfMinTimeIsLessThanMaxTime() {
        Boolean canCheckMinimumDate = minimumTimeValidationPair.getKey();
        Boolean canCheckMaximumDate = maximumTimeValidationPair.getKey();

        if ( canCheckMinimumDate && canCheckMaximumDate) {
            LocalTime allowedMinimumTime = minimumTimeValidationPair.getValue();
            LocalTime allowedMaximumTime = maximumTimeValidationPair.getValue();

            if (allowedMinimumTime.isAfter(allowedMaximumTime)) {
                String errorMessage = "%s MinTime Can Not Be Greater Than %s MaxTime".formatted(
                        allowedMinimumTime, allowedMaximumTime
                );
                errors.rejectValue("validationRules", errorMessage);
            }
        }
    }

}
