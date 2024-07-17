package com.learn.mapper;

import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormFieldValidationRule;
import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import com.learn.dto.response.HtmlFormFieldResponse;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.HtmlForm;
import com.learn.entity.HtmlFormField;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HtmlFormMapper {

    public HtmlForm fromCreationPayload(HtmlFormCreationPayload payload) {
        if (payload == null) {
            return null;
        }

        HtmlForm htmlForm = new HtmlForm();

        htmlForm.setName(payload.getName());
        htmlForm.setRemark(payload.getRemark());

        return htmlForm;
    }

    public HtmlFormResponse fromHtmlForm(HtmlForm htmlForm) {
        if (htmlForm == null) {
            return null;
        }

        HtmlFormResponse htmlFormResponse = new HtmlFormResponse();

        htmlFormResponse.setId(htmlForm.getId());
        htmlFormResponse.setName(htmlForm.getName());
        htmlFormResponse.setRemark(htmlForm.getRemark());
        htmlFormResponse.setFormStatus(htmlForm.getFormStatus());
        htmlFormResponse.setHtmlFormFields(htmlFormFieldSetToHtmlFormFieldResponseSet(htmlForm.getHtmlFormFields()));

        return htmlFormResponse;
    }

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

    public HtmlFormFieldResponse fromHtmlFormField(HtmlFormField htmlFormField) {
        if (htmlFormField == null) {
            return null;
        }

        HtmlFormFieldResponse htmlFormFieldResponse = new HtmlFormFieldResponse();
        htmlFormFieldResponse.setId(htmlFormField.getId());
        htmlFormFieldResponse.setName(htmlFormField.getName());
        htmlFormFieldResponse.setType(htmlFormField.getType());
        htmlFormFieldResponse.setLabel(htmlFormField.getLabel());

        Map<FormFieldValidationRule, String> validationRules = htmlFormField.getValidationRules();
        if (!validationRules.isEmpty()) {
            htmlFormFieldResponse.setValidationRules(new LinkedHashMap<>(validationRules));
        }

        htmlFormFieldResponse.setFormFieldStatus(htmlFormField.getFormFieldStatus());
        htmlFormFieldResponse.setRemarks(htmlFormField.getRemarks());
        htmlFormFieldResponse.setPlaceHolder(htmlFormField.getPlaceHolder());
        htmlFormFieldResponse.setHelpDescription(htmlFormField.getHelpDescription());
        htmlFormFieldResponse.setCreatedDate(htmlFormField.getCreatedDate());
        htmlFormFieldResponse.setLastModifiedDate(htmlFormField.getLastModifiedDate());

        htmlFormFieldResponse.setVersion(htmlFormField.getVersion());

        return htmlFormFieldResponse;
    }


    private List<HtmlFormFieldResponse> htmlFormFieldSetToHtmlFormFieldResponseSet(Set<HtmlFormField> htmlFormFields) {
        if (htmlFormFields.isEmpty()) {
            return List.of();
        }
        List<HtmlFormFieldResponse> responses = new ArrayList<>();

        for (HtmlFormField htmlFormField : htmlFormFields) {
            HtmlFormFieldResponse htmlFormFieldResponse = fromHtmlFormField(htmlFormField);
            responses.add(htmlFormFieldResponse);
        }

        return responses;
    }

}
