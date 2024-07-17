package com.learn.service;

import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormStatus;
import com.learn.dto.request.SubmitDynamicFormPayload;
import com.learn.entity.FilledHtmlForm;
import com.learn.entity.HtmlForm;
import com.learn.entity.HtmlFormField;
import com.learn.entity.User;
import com.learn.exception.ApplicationException;
import com.learn.repository.FilledHtmlFormRepository;
import com.learn.repository.HtmlFormFieldRepository;
import com.learn.repository.HtmlFormRepository;
import com.learn.repository.UserRepository;
import com.learn.utils.ExceptionHelperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class HtmlFormService {
    private final HtmlFormRepository htmlFormRepository;
    private final HtmlFormFieldRepository htmlFormFieldRepository;
    private final UserRepository userRepository;
    private final FilledHtmlFormRepository filledHtmlFormRepository;


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

    @Transactional
    public FilledHtmlForm submitForm(SubmitDynamicFormPayload payload) {
        Long formId = payload.getFormId();
        Long userId = payload.getUserId();

        HtmlForm htmlForm = htmlFormRepository.findById(formId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlForm", formId));

        if (!htmlForm.isActive()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "HtmlForm Is Not In Active State, Can't Accept Submission"
            );
        }

        Set<String> submittedValuesForFields = payload.getFieldValues().keySet();
        List<String> invalidFormFields = submittedValuesForFields.stream() // Checking if the Fields sent in the Request are actually there in the form
                .filter(fieldName -> htmlForm.htmlFormFieldHavingName(fieldName).isEmpty())
                .toList();

        if (!invalidFormFields.isEmpty()) { // If there are some FormField sent in the request that are not in the DataBase Form, then throw exception
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid FormFields Provided:: " + invalidFormFields);
        }

        List<String> inActiveFieldSubmissions = submittedValuesForFields.stream()
                .filter(fieldName -> htmlForm.htmlFormFieldHavingName(fieldName).isEmpty())
                .toList();

        if (!inActiveFieldSubmissions.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "InActive Fields Submission:: " + inActiveFieldSubmissions
            );
        }

        Map<String, Object> fieldValues = payload.getFieldValues();
        Map<String, Boolean> failedValidationResults = new HashMap<>();

        fieldValues.forEach((fieldName, fieldValue) -> {
            // TODO : Validation and filling up of failedValidationResults

        });

        if (!failedValidationResults.isEmpty()) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "InValid Values Found In Fields:: " + failedValidationResults.keySet()
            );
        }

        User user = userRepository.getReferenceById(userId);

        FilledHtmlForm filledHtmlForm = new FilledHtmlForm();
        filledHtmlForm.setUser(user);
        filledHtmlForm.setHtmlForm(htmlForm);
        filledHtmlForm.setFormFieldValues(payload.getFieldValues());

        return filledHtmlFormRepository.save(filledHtmlForm);
    }

}
