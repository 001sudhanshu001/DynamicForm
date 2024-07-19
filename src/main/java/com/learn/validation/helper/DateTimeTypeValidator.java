package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;

import static com.learn.constants.FormFieldValidationRule.*;

public class DateTimeTypeValidator implements FieldValidator{

    private static final Set<FormFieldValidationRule> APPLICABLE_RULES =
            Set.of(REQUIRED, MIN_DATE_TIME, MAX_DATE_TIME);

    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    private Pair<Boolean, LocalDateTime> minimumDateValidationPair = Pair.of(Boolean.FALSE, null);
    private Pair<Boolean, LocalDateTime> maximumDateValidationPair = Pair.of(Boolean.FALSE, null);

    public DateTimeTypeValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
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
        Set<Map.Entry<FormFieldValidationRule, String>> ruleEntries = validationRules.entrySet();
        for (Map.Entry<FormFieldValidationRule, String> ruleEntry: ruleEntries) {
            FormFieldValidationRule rule = ruleEntry.getKey();
            String ruleValue = ruleEntry.getValue();
            if (!APPLICABLE_RULES.contains(rule)) {
                errors.rejectValue(
                        "validationRules[" + rule.name() + "]",
                        "Rule Not Applicable On Date Type FormField"
                );
                continue;
            }
            if (MIN_DATE_TIME.equals(rule)) {
                checkMinDateValidationValue(errors, ruleValue);
            } else if (MAX_DATE_TIME.equals(rule)) {
                checkMaxDateValidationValue(errors, ruleValue);
            }
        }
    }

    private void checkMinDateValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            LocalDateTime parsedDate = LocalDateTime.parse(ruleValue);
            minimumDateValidationPair = Pair.of(Boolean.TRUE, parsedDate);
        } catch (DateTimeParseException exception) {
            errors.rejectValue(
                    "validationRules[" + MIN_DATE_TIME + "]",
                    "Invalid Value Provided For DateTime Rule:: " + MIN_DATE_TIME + " => " + ruleValue
            );
        }
    }

    private void checkMaxDateValidationValue(Errors errors, String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            return;
        }
        try {
            LocalDateTime parsedDate = LocalDateTime.parse(ruleValue);
            maximumDateValidationPair = Pair.of(Boolean.TRUE, parsedDate);
        } catch (DateTimeParseException exception) {
            errors.rejectValue(
                    "validationRules[" + MAX_DATE_TIME + "]",
                    "Invalid Value Provided For DateTime Rule:: " + MAX_DATE_TIME + " => " + ruleValue
            );
        }
    }

    private void checkIfMinDateIsLessThanMaxDate() {
        Boolean canCheckMinimumDate = minimumDateValidationPair.getKey();
        Boolean canCheckMaximumDate = maximumDateValidationPair.getKey();

        Boolean applyFilter = canCheckMinimumDate && canCheckMaximumDate;
        if (BooleanUtils.isTrue(applyFilter)) {
            LocalDateTime minDateTime = minimumDateValidationPair.getValue();
            LocalDateTime maxDateTime = maximumDateValidationPair.getValue();

            if (minDateTime.isAfter(maxDateTime)) {
                String errorMessage = "%s MinDateTime Can Not Be Greater Than %s MaxDateTime".formatted(
                        minDateTime, maxDateTime
                );
                errors.rejectValue("validationRules", errorMessage);
            }
        }
    }

}
