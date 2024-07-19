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
        } else if (NUMBER.equals(type)) {
            return new NumberTypeValidator(payload, errors);
        } else if (RADIO.equals(type)) {
            return new RadioTypeValidator(payload, errors);
        } else if (CHECKBOX.equals(type)) {
            return new CheckBoxTypeValidator(payload, errors);
        } else if (DATE.equals(type)) {
            return new DateTypeValidator(payload, errors);
        } else if (TIME.equals(type)) {
            return new TimeTypeValidator(payload, errors);
        } else if (DATETIME_LOCAL.equals(type)) {
            return new DateTimeTypeValidator(payload, errors);
        } else if (FILE.equals(type)) {
            return new FileTypeValidator(payload, errors);
        }

        // TODO : Implement Other Validators

        return new NoOpValidator();
    }
}
