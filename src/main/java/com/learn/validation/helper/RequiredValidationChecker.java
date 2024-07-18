package com.learn.validation.helper;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

class RequiredValidationChecker {

    public static void validateRequiredRuleValidation(HtmlFormFieldCreationPayload payload, Errors errors) {
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

}
