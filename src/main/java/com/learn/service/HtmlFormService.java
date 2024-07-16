package com.learn.service;

import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormStatus;
import com.learn.entity.HtmlForm;
import com.learn.entity.HtmlFormField;
import com.learn.exception.ApplicationException;
import com.learn.repository.HtmlFormFieldRepository;
import com.learn.repository.HtmlFormRepository;
import com.learn.utils.ExceptionHelperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class HtmlFormService {
    private final HtmlFormRepository htmlFormRepository;
    private final HtmlFormFieldRepository htmlFormFieldRepository;


    @Transactional
    public HtmlForm save(HtmlForm htmlForm) {
        return htmlFormRepository.save(htmlForm);
    }

    @Transactional
    public HtmlFormField saveFormField(HtmlFormField htmlFormField) {
        Long fieldBelongToFormId = htmlFormField.getFormId();
        HtmlForm htmlForm = htmlFormRepository.findById(fieldBelongToFormId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlForm", fieldBelongToFormId));
        htmlForm.addHtmlFormField(htmlFormField);
        HtmlForm updatedHtmlForm = htmlFormRepository.save(htmlForm);
        return updatedHtmlForm.htmlFormFieldHavingName(htmlFormField.getName()).orElseThrow();
    }

    @Transactional
    public void makeFormActive(Long formId) {
        HtmlForm htmlForm = htmlFormRepository.findById(formId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlForm", formId));

        Set<HtmlFormField> htmlFormFields = htmlForm.getHtmlFormFields();

        if (htmlFormFields.isEmpty()) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "Required At Least One Form Field To Activate Form"
            );
        }
        Optional<HtmlFormField> probablyActiveFormField = htmlFormFields.stream()
                .filter(HtmlFormField::isActive)
                .findFirst();
        if (probablyActiveFormField.isEmpty()) {
            // TODO Implementing Global Exception Handler
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "Required At Least One Active Form Field To Activate Form"
            );
        }
        htmlForm.setFormStatus(FormStatus.ACTIVE);
        htmlFormRepository.save(htmlForm);
    }

    @Transactional
    public void makeFormFieldActive(Long formFieldId) {
        HtmlFormField htmlFormField = htmlFormFieldRepository.findById(formFieldId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlFormField", formFieldId));

        htmlFormField.setFormFieldStatus(FormFieldStatus.ACTIVE);
        htmlFormFieldRepository.save(htmlFormField);
    }

    public HtmlForm fetchFormToFill(Long formId) {
        HtmlForm htmlForm = htmlFormRepository.findById(formId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlForm", formId));
        if (!htmlForm.isActive()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "HtmlForm Is Not Active At The Moment");
        }
        return htmlForm;
    }

}
