package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import static com.learn.constants.FormFieldValidationRule.*;

public class DateTypeValidator implements FieldValidator{

    private static final Set<FormFieldValidationRule> applicableRules =
            Set.of(REQUIRED, MIN_DATE, MAX_DATE);

    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    private Pair<Boolean, LocalDate> minimumDateValidationPair = Pair.of(Boolean.FALSE, null);
    private Pair<Boolean, LocalDate> maximumDateValidationPair = Pair.of(Boolean.FALSE, null);

    public DateTypeValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
        this.payload = payload;
        this.errors = errors;
    }

    @Override
    public void validate() {
        RequiredValidationChecker.validateRequiredRuleValidation(payload, errors);
        validateApplicableRules();
        checkIfMinDateIsLessThanMaxDate();
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
                        "Rule Not Applicable On Date Type FormField"
                );
                continue;
            }

            if (MIN_DATE.equals(rule)) {
                checkMinDateValidationValue(errors, ruleValue);
            } else if (MAX_DATE.equals(rule)) {
                checkMaxDateValidationValue(errors, ruleValue);
            }
        }
    }

    private void checkMinDateValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            LocalDate parsedDate = LocalDate.parse(ruleValue);
            minimumDateValidationPair = Pair.of(Boolean.TRUE, parsedDate);
        } catch (DateTimeParseException exception) {
            errors.rejectValue(
                    "validationRules[" + MIN_DATE + "]",
                    "Invalid Value Provided For " + MIN_DATE + " => " + ruleValue
            );
        }
    }

    private void checkMaxDateValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            LocalDate parsedDate = LocalDate.parse(ruleValue);
            maximumDateValidationPair = Pair.of(Boolean.TRUE, parsedDate);
        } catch (DateTimeParseException exception) {
            errors.rejectValue(
                    "validationRules[" + MAX_DATE + "]",
                    "Invalid Value Provided For " + MAX_DATE + " => " + ruleValue
            );
        }
    }

    private void checkIfMinDateIsLessThanMaxDate() {
        Boolean canCheckMinimumDate = minimumDateValidationPair.getKey();
        Boolean canCheckMaximumDate = maximumDateValidationPair.getKey();

        if (canCheckMinimumDate && canCheckMaximumDate) {
            LocalDate allowedMinimumDate = minimumDateValidationPair.getValue();
            LocalDate allowedMaximumDate = maximumDateValidationPair.getValue();

            if (allowedMinimumDate.isAfter(allowedMaximumDate)) {
                String errorMessage = "%s MinDate Can Not Be Greater Than %s MaxDate".formatted(
                        allowedMinimumDate, allowedMaximumDate
                );
                errors.rejectValue("validationRules", errorMessage);
            }
        }
    }
}
