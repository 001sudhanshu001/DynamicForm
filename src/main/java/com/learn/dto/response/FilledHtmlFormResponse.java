package com.learn.dto.response;

import com.learn.entity.FilledHtmlForm;
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
}
