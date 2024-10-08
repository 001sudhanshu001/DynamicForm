package com.learn.entity.validator;

import com.learn.constants.FormFieldValidationRule;
import com.learn.constants.InputType;
import com.learn.entity.HtmlFormField;

import java.util.Map;

public class FieldValueValidatorFactory {

    private FieldValueValidatorFactory(){}


    public static FieldValueValidator createValidatorFor(HtmlFormField formField, Object formFieldValue) {
        InputType type = formField.getType();
        String name = formField.getName();
        Map<FormFieldValidationRule, String> validationRules = formField.getValidationRules();

        if (type.equals(InputType.TEXT)) {
            String fieldValueAsString = formFieldValue != null ? String.valueOf(formFieldValue) : null;

            return new TextTypeValueValidator(name, validationRules, fieldValueAsString);
        } else if (type.equals(InputType.NUMBER)) {
            return new NumberTypeValueValidator(name, validationRules, formFieldValue);
        } else if (type.equals(InputType.RADIO)) {
            Map<String, String> displayOptions = formField.getDisplayOptions();

            return new RadioTypeValueValidator(name, validationRules, displayOptions, formFieldValue);
        } else if (type.equals(InputType.CHECKBOX)) {
            Map<String, String> displayOptions = formField.getDisplayOptions();

            return new CheckBoxTypeValueValidator(name, validationRules, displayOptions, formFieldValue);
        } else if (type.equals(InputType.DATE)) {
            return new DateValueValidator(name, validationRules, getFieldValueAsString(formFieldValue));
        } else if (type.equals(InputType.TIME)) {
            return new TimeValueValidator(name, validationRules, getFieldValueAsString(formFieldValue));
        } else if (type.equals(InputType.DATETIME_LOCAL)) {
            return new DateTimeValueValidator(name, validationRules, getFieldValueAsString(formFieldValue));
        }  else if (type.equals(InputType.FILE)) {
            return new FileTypeValueValidator(name, validationRules, formFieldValue);
        }
        // TODO : Implementation of FileTypeValidator
        return new NoOpValueValidator();
    }

    private static String getFieldValueAsString(Object formFieldValue) {
        return formFieldValue != null ? String.valueOf(formFieldValue) : null;
    }

}
