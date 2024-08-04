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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/dynamic-form")
@Slf4j
@RequiredArgsConstructor
public class HtmlFormController {
    // TODO : Move DB Validations in Service Layer
    private final HtmlFormService htmlFormService;
    private final HtmlFormMapper htmlFormMapper;
    private final HtmlFormFieldCreationValidator htmlFormFieldCreationValidator;

    @PostMapping("/create-form")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> addFormField(@RequestBody @Valid HtmlFormFieldCreationPayload payload)
            throws NoSuchMethodException, MethodArgumentNotValidException {
        Long formId = payload.getFormId();
        String userName = getAuthenticatedUserName();

        boolean whetherFormBelongsToThisUser = htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId);
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
            Method method = HtmlFormController.class.
                    getDeclaredMethod("addFormField", HtmlFormFieldCreationPayload.class);
            MethodParameter methodParameter = new MethodParameter(method, 0);
            BindingResult bindingResult = bindException.getBindingResult();
            throw new MethodArgumentNotValidException(methodParameter, bindingResult);
        }

        HtmlFormField htmlFormField = htmlFormMapper.fromHtmlFormFieldCreationPayload(payload);
        HtmlFormField savedHtmlFormField = htmlFormService.saveFormField(htmlFormField);
        return new ResponseEntity<>(htmlFormMapper.fromHtmlFormField(savedHtmlFormField), HttpStatus.CREATED);
    }

    @PatchMapping("/make-form-active/{formId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> makeFormActive(@PathVariable Long formId) {
        String userName = getAuthenticatedUserName();

        boolean whetherFormBelongsToThisUser = htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId);
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
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?>  makeFormFieldActive(@PathVariable Long formId,
                                                  @PathVariable Long formFieldId) {

        String userName = getAuthenticatedUserName();
        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId, formFieldId);

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
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> makeFormFieldInActive(@PathVariable Long formId,
                                                        @PathVariable Long formFieldId) {

        String userName = getAuthenticatedUserName();
        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId, formFieldId);

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


    @DeleteMapping("/delete-form-field/{formId}/{formFieldId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> deleteFormField(@PathVariable Long formId,
                                                   @PathVariable Long formFieldId) {

        String userName = getAuthenticatedUserName();
        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId, formFieldId);

        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        htmlFormService.deleteFormField(formFieldId);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping("/fetch-form-to-fill/{formId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'STUDENT', 'PARENTS')")
    public ResponseEntity<?> fetchFormToFill(@PathVariable Long formId) {
        HtmlForm htmlForm = htmlFormService.fetchFormToFill(formId);
        HtmlFormResponse htmlFormResponse = htmlFormMapper.fromHtmlForm(htmlForm);
        return ResponseEntity.ok(htmlFormResponse);
    }

    @PostMapping("/submit-form")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'STUDENT', 'PARENTS')")
    public ResponseEntity<FilledHtmlFormResponse> submitForm(@RequestBody @Valid SubmitDynamicFormPayload payload) {
        String userName = getAuthenticatedUserName();

        FilledHtmlForm filledHtmlForm = htmlFormService.submitForm(payload, userName);
        return new ResponseEntity<>(new FilledHtmlFormResponse(filledHtmlForm), HttpStatus.ACCEPTED);
    }

    @PutMapping("/update-form") // This is to update the filled form
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'STUDENT', 'PARENTS')")
    public ResponseEntity<?> updateForm(@RequestBody @Valid SubmitDynamicFormPayload payload) {
        String userName = getAuthenticatedUserName();

       // Authorization is Handled in Service layer

        FilledHtmlForm updatedForm = htmlFormService.updateFilledForm(payload, userName);
        return new ResponseEntity<>(new FilledHtmlFormResponse(updatedForm), HttpStatus.ACCEPTED);
    }

    @GetMapping("/fetch-filled-forms")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'STUDENT', 'PARENTS')")
    public ResponseEntity<?> fetchFilledForms(@RequestParam(name = "pageNum", required = false, defaultValue = "1")
                                                  @Min(value = 1) Integer pageNum,
                                @RequestParam(name = "pageSize", required = false, defaultValue = "5") @Min(value = 1)
                                    @Max(value = 5) Integer pageSize) {

        String userName = getAuthenticatedUserName();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Page<FilledHtmlForm> filledForms = htmlFormService.getAllFilledFormsOfUser(userName, pageable);

        // Can add logic around the pagination

        List<FilledHtmlForm> content = filledForms.getContent();
        List<FilledHtmlFormResponse> response = new ArrayList<>();

        for(FilledHtmlForm filledHtmlForm : content) {
            response.add(new FilledHtmlFormResponse(filledHtmlForm));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/fetch-filled-form/{Id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'STUDENT', 'PARENTS')")
    public ResponseEntity<?> fetchFilledFormById(@PathVariable("Id") Long id) {
        String userName = getAuthenticatedUserName();

        FilledHtmlForm filledForm = htmlFormService.getFilledFormById(id, userName);

        return ResponseEntity.ok(new FilledHtmlFormResponse(filledForm));
    }

    @PatchMapping("/change-display-name")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> changeDisplayName(@RequestBody @Valid ChangeDisplayNamePayload payload) {

        String userName = getAuthenticatedUserName();
        Long formId = payload.getFormId();

        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId);

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
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> setFieldsDisplayOrder(@RequestBody @Valid FieldsDisplayOrderPayload payload) {
        String userName = getAuthenticatedUserName(); // TODO : Handle, If userName is NUll
        Long formId = payload.getFormId();

        boolean whetherFormBelongsToThisUser =
                htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId);

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

    @PatchMapping("/update-form-field")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    @SneakyThrows
    public ResponseEntity<?> updateFormField(
            @RequestBody @Valid HtmlFormFieldCreationPayload payload) {

        Long formId = payload.getFormId();
        String userName = getAuthenticatedUserName();

        boolean whetherFormBelongsToThisUser = htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId);
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
            Method method = new Object() {}.getClass().getEnclosingMethod();
            MethodParameter methodParameter = new MethodParameter(method, 0);
            BindingResult bindingResult = bindException.getBindingResult();
            throw new MethodArgumentNotValidException(methodParameter, bindingResult);
        }

        HtmlFormField updatedFormField = htmlFormService.updateFormField(payload);
        return ResponseEntity.ok(htmlFormMapper.fromHtmlFormField(updatedFormField));
    }

    @PostMapping("/filter-filled-forms")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> filterFilledForms(@RequestBody @Valid AppliedDynamicFilter filter) {
        Long formId = filter.getFormId();
        String userName = getAuthenticatedUserName();

        boolean whetherFormBelongsToThisUser = htmlFormService.checkWhetherFormBelongsToThisAdmin(userName, formId);
        if(!whetherFormBelongsToThisUser) {
            ErrorResponse errorResponse = new ErrorResponse(
                    new Date(), HttpServletResponse.SC_NOT_FOUND,
                    "Not Found", "The form not Found"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        List<FilledHtmlFormResponse> filledHtmlFormResponses = htmlFormService.filterFilledForms(filter);

        return ResponseEntity.ok(filledHtmlFormResponses);
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
