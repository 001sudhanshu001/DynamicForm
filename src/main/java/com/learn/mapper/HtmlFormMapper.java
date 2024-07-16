package com.learn.mapper;

import com.learn.constants.FormFieldStatus;
import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import com.learn.dto.response.HtmlFormFieldResponse;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.HtmlForm;
import com.learn.entity.HtmlFormField;
import org.mapstruct.Mapper;

@Mapper
public abstract class HtmlFormMapper {
    public abstract HtmlForm fromCreationPayload(HtmlFormCreationPayload payload);
    public abstract HtmlFormResponse fromHtmlForm(HtmlForm htmlForm);

    public HtmlFormField fromHtmlFormFieldCreationPayload(HtmlFormFieldCreationPayload payload) {
        HtmlFormField htmlFormField = new HtmlFormField();

        htmlFormField.setFormId(payload.getFormId());
        htmlFormField.setName(payload.getName());
        htmlFormField.setType(payload.getType());
        htmlFormField.setLabel(payload.getLabel());
        htmlFormField.setValidationRules(payload.getValidationRules());
        htmlFormField.setFormFieldStatus(FormFieldStatus.IN_ACTIVE);
        htmlFormField.setRemarks(payload.getRemarks());
        htmlFormField.setPlaceHolder(payload.getPlaceHolder());
        htmlFormField.setHelpDescription(payload.getHelpDescription());
        htmlFormField.setSortingOrder(Integer.MAX_VALUE);

        return htmlFormField;
    }

    public abstract HtmlFormFieldResponse fromHtmlFormField(HtmlFormField htmlFormField);
}
