package com.learn.controller;

import com.learn.constants.FormStatus;
import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.HtmlForm;
import com.learn.mapper.HtmlFormMapper;
import com.learn.service.HtmlFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
