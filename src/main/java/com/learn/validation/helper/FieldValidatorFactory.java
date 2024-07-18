package com.learn.validation.helper;

import com.learn.constants.InputType;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.springframework.validation.Errors;

public class FieldValidatorFactory {
    private FieldValidatorFactory(){}

    public static FieldValidator createValidatorFor(HtmlFormFieldCreationPayload payload, Errors errors) {
        InputType type = payload.getType();

        if(type.equals(InputType.TEXT)) {
            return new TextFieldValidator(payload, errors);
        }


        // TODO : Implement Other Validators

        return new NoOpValidator();
    }
}
