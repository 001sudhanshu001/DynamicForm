package com.learn.service;

import com.learn.constants.FormFieldStatus;
import com.learn.constants.FormStatus;
import com.learn.dto.internal.AddFormFieldResult;
import com.learn.dto.internal.FieldStatusChangeResult;
import com.learn.dto.internal.FieldValidationResult;
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

        // This will add the Field in the form is Field is Unique and AddFormFieldResult success will true
        // Otherwise it will add failure message
        AddFormFieldResult addFormFieldResult = htmlForm.addHtmlFormField(htmlFormField);

        if(!addFormFieldResult.isSuccess()) {
            // TODO : Implement Global Exception Handler to Handle the Exception and return a Proper response
            throw new ApplicationException(HttpStatus.BAD_REQUEST, addFormFieldResult.getFailMessage());
        }
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
    public void changeFormFieldStatus(Long formId, Long formFieldId, boolean requestToMakeActive) {
        HtmlForm htmlForm = htmlFormRepository.findById(formId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlForm", formId));

        FieldStatusChangeResult result;

        if(requestToMakeActive){
            result = htmlForm.changeFormFieldStatus(formFieldId, true);
        } else {
            result = htmlForm.changeFormFieldStatus(formFieldId, false);
        }

        if (!result.isSuccess()) {
            throw new ApplicationException(result.getHttpStatus(), result.getFailMessage());
        }

        if(requestToMakeActive) {
            htmlFormFieldRepository.updateActiveStatus(formFieldId, FormFieldStatus.ACTIVE);
        }else {
            htmlFormFieldRepository.updateActiveStatus(formFieldId, FormFieldStatus.IN_ACTIVE);
        }
    }

    public HtmlForm fetchFormToFill(Long formId) {
        HtmlForm htmlForm = htmlFormRepository.getHtmlFormWithActiveFormFields(formId, FormFieldStatus.ACTIVE)
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

        validateSubmittedForm(payload, htmlForm);

        User user = userRepository.getReferenceById(userId);

        FilledHtmlForm filledHtmlForm = new FilledHtmlForm();
        filledHtmlForm.setUser(user);
        filledHtmlForm.setHtmlForm(htmlForm);
        filledHtmlForm.setFormFieldValues(payload.getFieldValues());

        return filledHtmlFormRepository.save(filledHtmlForm);
    }

    @Transactional
    public FilledHtmlForm updateForm(SubmitDynamicFormPayload payload) {
        Long formId = payload.getFormId();
        Optional<FilledHtmlForm> optionalFilledHtmlForm =
                filledHtmlFormRepository.findByFormIdAndUserId(formId, payload.getUserId());
        if (optionalFilledHtmlForm.isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "FilledHtmlForm Not Found");
        }

        HtmlForm htmlForm = htmlFormRepository.findById(formId)
                .orElseThrow(ExceptionHelperUtils.notFoundException("HtmlForm", formId));

        validateSubmittedForm(payload, htmlForm);

        FilledHtmlForm filledHtmlForm = optionalFilledHtmlForm.get();
        filledHtmlForm.setFormFieldValues(payload.getFieldValues());

        return filledHtmlFormRepository.save(filledHtmlForm);
    }

    private void validateSubmittedForm(SubmitDynamicFormPayload payload, HtmlForm htmlForm) {
        if (!htmlForm.isActive()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "HtmlForm Is Not In Active State, Can't Accept Submission"
            );
        }

        Set<String> submittedValuesForFields = payload.getFieldValues().keySet();
        List<String> invalidFormFields = submittedValuesForFields.stream() // Checking if the Fields sent in the Request are actually there in the form
                .filter(fieldName -> htmlForm.htmlFormFieldHavingName(fieldName).isEmpty())
                .toList();
        if (!invalidFormFields.isEmpty()) { // If there are some FormField sent in the request that are not in the Database Form, then throw exception
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Invalid FormFields Provided:: " + invalidFormFields);
        }

        // If some fields are inactive and theses are sent as a request
        List<String> inActiveFieldSubmissions = submittedValuesForFields.stream()
                .filter(fieldName -> htmlForm.htmlFormFieldHavingName(fieldName, HtmlFormField::isActive).isEmpty())
                .toList();
        if (!inActiveFieldSubmissions.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "InActive Fields Submission:: " + inActiveFieldSubmissions
            );
        }

        // If Required fields are not provided
        Map<String, Object> fieldFromPayload = payload.getFieldValues();
        Set<String> providedFields = fieldFromPayload.keySet();
        Set<String> missingRequiredFields = htmlForm.missingRequiredFields(providedFields);
        if (!missingRequiredFields.isEmpty()) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "Required Fields Missing In Submission:: " + missingRequiredFields
            );
        }

        // Checking for each field(Key, value) in the form payload if the value is as per the Specified Rules
        Set<String> failedValidationResults = new HashSet<>();

        fieldFromPayload.forEach((fieldName, fieldValue) -> {
            FieldValidationResult fieldValidationResult = htmlForm.validateFieldValue(fieldName, fieldValue);

            if (!fieldValidationResult.isSuccess()) {
                failedValidationResults.add(fieldValidationResult.toString());
            }
        });

        if (!failedValidationResults.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, failedValidationResults.toString());
        }
    }
}
