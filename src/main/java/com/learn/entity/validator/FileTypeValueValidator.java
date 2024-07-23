package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.internal.FieldValidationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class FileTypeValueValidator implements FieldValueValidator {
    private final String fieldName;
    private final Map<FormFieldValidationRule, String> validationRules;
    private final Object formFieldValue;

    private FieldValidationResult result = FieldValidationResult.builder().success(true).build();
    private boolean tryNextValidation = true;

    @Override
    public FieldValidationResult validate() {
        validateValueType();

        if (tryNextValidation) {
            validateAgainstRequiredRule();
        } else {
            return result;
        }
        if (tryNextValidation) {
            validateAgainstMinFileRule();
        } else {
            return result;
        }
        if (tryNextValidation) {
            validateAgainstMaxFileRule();
        } else {
            return result;
        }

        return result;
    }

    private void validateValueType() {
        if (Objects.isNull(formFieldValue)) {
            return;
        }
        boolean isInstanceOfList = formFieldValue instanceof List<?>;
        if (BooleanUtils.isNotTrue(isInstanceOfList)) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .failMessage("Required Array Of Media Ids")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstRequiredRule() {
        String requiredOrNot = validationRules.get(FormFieldValidationRule.REQUIRED);
        boolean required = BooleanUtils.toBoolean(requiredOrNot);

        if (BooleanUtils.isNotTrue(required)) {
            // do not check value if not required
            return;
        }

        List<?> providedIds = formFieldValue instanceof List ? (List<?>) formFieldValue
                : Collections.emptyList();

        if (!providedIds.isEmpty()) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("At Least One Media File Required")
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMinFileRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MIN_FILES);
        int minFiles = Integer.parseInt(validationValue);

        List<?> mediaFileIds = formFieldValue instanceof List ? (List<?>) formFieldValue
                : Collections.emptyList();
        if (minFiles > mediaFileIds.size()) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("MinFiles Required:: " + minFiles + " But Found:: " + mediaFileIds.size())
                    .build();
            tryNextValidation = false;
        }
    }

    private void validateAgainstMaxFileRule() {
        String validationValue = validationRules.get(FormFieldValidationRule.MAX_FILES);
        int maxFilesAllowed = Integer.parseInt(validationValue);

        List<?> mediaFileIds = formFieldValue instanceof List ? (List<?>) formFieldValue
                : Collections.emptyList();
        if (maxFilesAllowed < mediaFileIds.size()) {
            result = FieldValidationResult.builder()
                    .success(false)
                    .fieldName(fieldName)
                    .failMessage("MaxFiles Allowed:: " + maxFilesAllowed + " But Found:: " + mediaFileIds.size())
                    .build();
            tryNextValidation = false;
        }
    }

}
