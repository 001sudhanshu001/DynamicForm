package com.learn.validation;

import com.learn.dto.request.HtmlFormFieldCreationPayload;
import com.learn.validation.helper.FieldValidator;
import com.learn.validation.helper.FieldValidatorFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class HtmlFormFieldCreationValidator implements Validator {

    // This validator is used when the user add a new Field in the form, this checks whether the added field is as per rules
    @Override
    public boolean supports(Class<?> clazz) {
        return HtmlFormFieldCreationPayload.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HtmlFormFieldCreationPayload payload = (HtmlFormFieldCreationPayload) target;
        FieldValidator fieldValidator = FieldValidatorFactory.createValidatorFor(payload, errors);
        fieldValidator.validate();
    }

}