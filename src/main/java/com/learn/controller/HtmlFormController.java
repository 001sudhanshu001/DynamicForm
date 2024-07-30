package com.learn.controller;

import com.learn.constants.FormStatus;
import com.learn.dto.dynamicfilter.AppliedDynamicFilter;
import com.learn.dto.request.*;
import com.learn.dto.response.FilledHtmlFormResponse;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.FilledHtmlForm;
import com.learn.entity.HtmlForm;
import com.learn.entity.HtmlFormField;
import com.learn.exception.ErrorResponse;
import com.learn.mapper.HtmlFormMapper;
import com.learn.service.HtmlFormService;
import com.learn.validation.HtmlFormFieldCreationValidator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Date;

@RestController
@RequestMapping("/dynamic-form")
@Slf4j
@RequiredArgsConstructor
public class HtmlFormController {
    private final HtmlFormService htmlFormService;
    private final HtmlFormMapper htmlFormMapper;
    private final HtmlFormFieldCreationValidator htmlFormFieldCreationValidator;

    @PostMapping("/create-form")
    public ResponseEntity<?> createForm(@RequestBody @Valid HtmlFormCreationPayload payload) {
        String userName = getAuthenticatedUserName();
        if(userName != null) {
            HtmlForm htmlForm = htmlFormMapper.fromCreationPayload(payload);

            htmlForm.setFormStatus(FormStatus.IN_ACTIVE); // Initially Form Will Be In-Active

            HtmlForm savedHtmlForm = htmlFormService.save(htmlForm, userName);
            return new ResponseEntity<>(htmlFormMapper.fromHtmlForm(savedHtmlForm), HttpStatus.CREATED);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                new Date(), HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized", "Full authentication is required to access this resource"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/add-form-field")
    public ResponseEntity<?> addFormField(@RequestBody @Valid HtmlFormFieldCreationPayload payload)
            throws NoSuchMethodException, MethodArgumentNotValidException {
        Long formId = payload.getFormId();
        String userName = getAuthenticatedUserName();

        boolean whetherFormBelongsToThisUser = htmlFormService.checkWhetherFormBelongsToThisUser(userName, formId);
        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        BindException bindException = new BindException(payload, "payload");
        htmlFormFieldCreationValidator.validate(payload, bindException);

        if (bindException.hasErrors()) {
            Method method = HtmlFormController.class.getDeclaredMethod("addFormField", HtmlFormFieldCreationPayload.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            BindingResult bindingResult = bindException.getBindingResult();
            throw new MethodArgumentNotValidException(methodParameter, bindingResult); // Handled in GlobalExceptionHandler
        }

        HtmlFormField htmlFormField = htmlFormMapper.fromHtmlFormFieldCreationPayload(payload);
        HtmlFormField savedHtmlFormField = htmlFormService.saveFormField(htmlFormField);
        return new ResponseEntity<>(htmlFormMapper.fromHtmlFormField(savedHtmlFormField), HttpStatus.CREATED);
    }

    @PatchMapping("/make-form-active/{formId}")
    public ResponseEntity<?> makeFormActive(@PathVariable Long formId) {
        String userName = getAuthenticatedUserName();

        boolean whetherFormBelongsToThisUser = htmlFormService.checkWhetherFormBelongsToThisUser(userName, formId);
        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        htmlFormService.makeFormActive(formId);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PatchMapping("/make-form-field-active/{formId}/{formFieldId}")
    public ResponseEntity<?>  makeFormFieldActive(@PathVariable Long formId,
                                                  @PathVariable Long formFieldId) {

        String userName = getAuthenticatedUserName();
        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisUser(userName, formId, formFieldId);

        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        htmlFormService.changeFormFieldStatus(formId, formFieldId, true);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @PatchMapping("/make-form-field-inactive/{formId}/{formFieldId}")
    public ResponseEntity<?> makeFormFieldInActive(@PathVariable Long formId,
                                                        @PathVariable Long formFieldId) {

        String userName = getAuthenticatedUserName();
        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisUser(userName, formId, formFieldId);

        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        htmlFormService.changeFormFieldStatus(formId, formFieldId, false);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping("/fetch-form-to-fill/{formId}")
    public ResponseEntity<?> fetchFormToFill(@PathVariable Long formId) {
        // TODO : Validate id this Form belongs to this user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userName = authentication.getName();
        }

        System.out.println("The username is " + userName);

        HtmlForm htmlForm = htmlFormService.fetchFormToFill(formId);
        HtmlFormResponse htmlFormResponse = htmlFormMapper.fromHtmlForm(htmlForm);
        return ResponseEntity.ok(htmlFormResponse);
    }

    @PostMapping("/submit-form")
    public ResponseEntity<FilledHtmlFormResponse> submitForm(@RequestBody @Valid SubmitDynamicFormPayload payload) {
        // TODO : The form will be submitted by a Student, Create Student Entity and update accordingly
        FilledHtmlForm filledHtmlForm = htmlFormService.submitForm(payload);
        return new ResponseEntity<>(new FilledHtmlFormResponse(filledHtmlForm), HttpStatus.ACCEPTED);
    }

    @PostMapping("/update-form") // This is to update the filled form
    public ResponseEntity<?> updateForm(@RequestBody @Valid SubmitDynamicFormPayload payload) {
            // TODO : The form will be `submitted` by a Student, Create Student Entity and update accordingly
        String userName = getAuthenticatedUserName();
        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisUser(userName, payload.getFormId());

        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        FilledHtmlForm updatedForm = htmlFormService.updateForm(payload);
        return ResponseEntity.ok(updatedForm);
    }

    @PatchMapping("/change-display-name")
    public ResponseEntity<?> changeDisplayName(@RequestBody @Valid ChangeDisplayNamePayload payload) {

        String userName = getAuthenticatedUserName();
        Long formId = payload.getFormId();

        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisUser(userName, formId);

        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        boolean result = htmlFormService.changeDisplayName(payload);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping("/change-display-order")
    public ResponseEntity<?> setFieldsDisplayOrder(@RequestBody @Valid FieldsDisplayOrderPayload payload) {
        String userName = getAuthenticatedUserName();
        Long formId = payload.getFormId();

        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisUser(userName, formId);

        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        HtmlForm htmlForm = htmlFormService.setFieldsDisplayOrder(payload);
        return ResponseEntity.ok(htmlFormMapper.fromHtmlForm(htmlForm));
    }


    private String getAuthenticatedUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = null;
        if (authentication != null && authentication.isAuthenticated()) {
            userName = authentication.getName();
        }

        return userName;
    }

}
