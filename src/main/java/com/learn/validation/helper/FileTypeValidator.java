package com.learn.validation.helper;

import com.learn.constants.FileType;
import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.learn.constants.FormFieldValidationRule.*;

public class FileTypeValidator implements FieldValidator{

    private static final Set<FormFieldValidationRule> applicableRules =
            Set.of(REQUIRED, ALLOWED_FILE_TYPES, MIN_FILES, MAX_FILES);

    // These Validation must be there creating a File FormField
    private static final Set<FormFieldValidationRule> requiredRules =
            Set.of(ALLOWED_FILE_TYPES, MIN_FILES, MAX_FILES);

    private final HtmlFormFieldCreationPayload payload;
    private final Errors errors;

    private Pair<Boolean, Integer> minimumFilesValidationPair = Pair.of(Boolean.FALSE, null);
    private Pair<Boolean, Integer> maximumFilesValidationPair = Pair.of(Boolean.FALSE, null);

    public FileTypeValidator(HtmlFormFieldCreationPayload payload, Errors errors) {
        this.payload = payload;
        this.errors = errors;
    }

    @Override
    public void validate() {
        RequiredValidationChecker.validateRequiredRuleValidation(payload, errors);
        validateIfRequiredValidationsProvidedOrNot();
        validateFileTypeFormField();
        checkIfMinFilesIsLessThanMaxFiles();
    }

    private void validateIfRequiredValidationsProvidedOrNot() {
        Map<FormFieldValidationRule, String> validationRules = payload.getValidationRules();
        Set<FormFieldValidationRule> providedRules = validationRules.keySet();
        List<FormFieldValidationRule> missedRules = new ArrayList<>();

        for (FormFieldValidationRule required : requiredRules) {
            if (!providedRules.contains(required)) {
                missedRules.add(required);
            }
        }

        if (!missedRules.isEmpty()) {
            errors.rejectValue("validationRules", "Validations Not Found In Payload::" + missedRules);
        }
    }

    private void validateFileTypeFormField() {
        Map<FormFieldValidationRule, String> validationRules = payload.getValidationRules();
        var ruleEntries = validationRules.entrySet();
        for (var ruleEntry: ruleEntries) {
            FormFieldValidationRule validationRule = ruleEntry.getKey();
            if (!applicableRules.contains(validationRule)) {
                errors.rejectValue(
                        "validationRules[" + validationRule.name() + "]",
                        "Rule Not Applicable On FILE Type FormField"
                );
            } else {
                if (validationRule.equals(ALLOWED_FILE_TYPES)) {
                    validateFileType(ruleEntry.getValue());
                } else if (validationRule.equals(MIN_FILES)) {
                    checkMinFilesValidationValue(ruleEntry.getValue());
                } else if (validationRule.equals(MAX_FILES)) {
                    checkMaxFilesValidationValue(ruleEntry.getValue());
                }
            }
        }
    }

    private void validateFileType(String validationValue) { // Allowed File Types will comma(,) separated
        if (StringUtils.isBlank(validationValue)) { // Allowed FileType is must
            errors.rejectValue(
                    "validationRules[ALLOWED_FILE_TYPES]",
                    "Required Allowed FileType For This Field"
            );
            return;
        }

        String[] allowedFileTypes = StringUtils.split(validationValue, ",");
        for (String fileTypeInString : allowedFileTypes) {
            // Check if there is enum constant with this fileType in FileType
            boolean validEnum = EnumUtils.isValidEnum(FileType.class, fileTypeInString);
            if (!validEnum) {
                errors.rejectValue(
                        "validationRules[ALLOWED_FILE_TYPES]",
                        "Invalid FileType Provided:: " + fileTypeInString
                );
            }
        }
    }

    private void checkMinFilesValidationValue(String ruleValue) {
        if (StringUtils.isBlank(ruleValue)) {
            errors.rejectValue(
                    "validationRules[MIN_FILES]",
                    "Required Min Files Allowed For This Field"
            );
            return;
        }
        try {
            int parsedInt = Integer.parseInt(ruleValue);
            if (parsedInt < 1) {
                errors.rejectValue(
                        "validationRules[MIN_LENGTH]",
                        "Required Positive Non-Zero Value For Rule:: MIN_FILES"
                );
            } else {
                minimumFilesValidationPair = Pair.of(Boolean.TRUE, parsedInt);
            }
        } catch (NumberFormatException exception) {
            errors.rejectValue(
                    "validationRules[MIN_LENGTH]",
                    "Invalid Value Provided For MIN_FILES"
            );
        }
    }

    private void checkMaxFilesValidationValue(String ruleValue) {
        String fieldName = "validationRules[MAX_FILES]";
        if (StringUtils.isBlank(ruleValue)) {
            errors.rejectValue(fieldName, "Required Max Files Allowed For This Field");
            return;
        }
        try {
            int parsedInt = Integer.parseInt(ruleValue);
            if (parsedInt < 1) {
                errors.rejectValue(fieldName, "Required Positive Non-Zero Value For Rule:: MAX_FILES");
            } else {
                maximumFilesValidationPair = Pair.of(Boolean.TRUE, parsedInt);
            }
        } catch (NumberFormatException exception) {
            errors.rejectValue(fieldName, "Invalid Value Provided For MAX_FILES");
        }
    }

    private void checkIfMinFilesIsLessThanMaxFiles() {
        Boolean canCheckMinimumLength = minimumFilesValidationPair.getKey();
        Boolean canCheckMaximumLength = maximumFilesValidationPair.getKey();

        if (canCheckMinimumLength && canCheckMaximumLength) {
            Integer allowedMinimumFileSize = minimumFilesValidationPair.getValue();
            Integer allowedMaximumFileSize = maximumFilesValidationPair.getValue();

            if (allowedMinimumFileSize > allowedMaximumFileSize) {
                String errorMessage = "%d MinFiles Can Not Be Greater Than %d MaxFiles".formatted(
                        allowedMinimumFileSize, allowedMaximumFileSize
                );
                errors.rejectValue("validationRules", errorMessage);
            }
        }
    }

}
