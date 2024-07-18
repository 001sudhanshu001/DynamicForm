package com.learn.validation.helper;

import com.learn.constants.InputType;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import org.springframework.validation.Errors;

import static com.learn.constants.InputType.*;

public class FieldValidatorFactory {
    private FieldValidatorFactory(){}

    public static FieldValidator createValidatorFor(HtmlFormFieldCreationPayload payload, Errors errors) {
        InputType type = payload.getType();

        if(type.equals(TEXT)) {
            return new TextFieldValidator(payload, errors);
        }else if (NUMBER.equals(type)) {
            return new NumberTypeValidator(payload, errors);
        }


        // TODO : Implement Other Validators

        return new NoOpValidator();
    }
}
