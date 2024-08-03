package com.learn.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn.entity.FilledHtmlForm;
import com.learn.utils.ObjectMapperUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class FilledHtmlFormResponse {
    private Long id;
    private Map<String, Object> formFieldValues;

    public FilledHtmlFormResponse(FilledHtmlForm filledHtmlForm) {
        id = filledHtmlForm.getId();
        formFieldValues = filledHtmlForm.getFormFieldValues();
    }

    public FilledHtmlFormResponse(Long id, String formFieldValues) throws JsonProcessingException {
        this.id = id;
        this.formFieldValues = ObjectMapperUtils.fromJsonString(formFieldValues, Map.class);
    }
}
