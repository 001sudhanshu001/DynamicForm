package com.learn.controller;

import com.learn.constants.FormStatus;
import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.dto.request.HtmlFormFieldCreationPayload;
import com.learn.dto.response.HtmlFormFieldResponse;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.HtmlForm;
import com.learn.entity.HtmlFormField;
import com.learn.mapper.HtmlFormMapper;
import com.learn.service.HtmlFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

@RestController
@RequestMapping("/dynamic-form")
@Slf4j
@RequiredArgsConstructor
public class HtmlFormController {
    private final HtmlFormService htmlFormService;
    private final HtmlFormMapper htmlFormMapper;

    @PostMapping("/create-form")
    public ResponseEntity<HtmlFormResponse> createForm(@RequestBody @Valid HtmlFormCreationPayload payload) {
        HtmlForm htmlForm = htmlFormMapper.fromCreationPayload(payload);

        htmlForm.setFormStatus(FormStatus.IN_ACTIVE); // Initially Form Will Be In-Active

        HtmlForm savedHtmlForm = htmlFormService.save(htmlForm);
        return new ResponseEntity<>(htmlFormMapper.fromHtmlForm(savedHtmlForm), HttpStatus.CREATED);
    }

    @PostMapping("/add-form-field")
    @SneakyThrows
    public ResponseEntity<HtmlFormFieldResponse> addFormField(@RequestBody @Valid HtmlFormFieldCreationPayload payload) {
        HtmlFormField htmlFormField = htmlFormMapper.fromHtmlFormFieldCreationPayload(payload);
        HtmlFormField savedHtmlFormField = htmlFormService.saveFormField(htmlFormField);
        return new ResponseEntity<>(htmlFormMapper.fromHtmlFormField(savedHtmlFormField), HttpStatus.CREATED);
    }

    @PostMapping("/make-form-active/{formId}")
    public ResponseEntity<?> makeFormActive(@PathVariable Long formId) {
        htmlFormService.makeFormActive(formId);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }
}
