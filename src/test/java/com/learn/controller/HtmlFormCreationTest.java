package com.learn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.constants.FormStatus;
import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.repository.HtmlFormRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HtmlFormCreationTest {

    @Autowired
    HtmlFormRepository htmlFormRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should Able To Create HtmlForm With IN_ACTIVE Status")
    void shouldAbleToCreateHtmlForm() throws Exception {
        HtmlFormCreationPayload payload = new HtmlFormCreationPayload();
        payload.setName("admission-form-step-1");
        payload.setRemark("This form will take basic detail from Parent After Registration");

        String body = objectMapper.writeValueAsString(payload);
        ResultActions resultActions = mockMvc.perform(post("/dynamic-form/create-form")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        );

        resultActions.andExpect(status().isCreated())
                        .andExpect(jsonPath("$.name", CoreMatchers.is(payload.getName())))
                        .andExpect(jsonPath("$.remark", CoreMatchers.is(payload.getRemark())))
                        .andExpect(jsonPath("$.formStatus", CoreMatchers.is(FormStatus.IN_ACTIVE.toString())))
                        .andExpect(jsonPath("$.htmlFormFields.size()", CoreMatchers.is(0)));

    }

    @Test
    @DisplayName("Should Able To Fetch Active HtmlForm")
    void shouldAbleToFetchActiveForm() throws Exception {
        // Given
        Long formId = 1L;

        // When
        ResultActions resultActions = mockMvc.perform(get("/dynamic-form/fetch-form-to-fill/{formId}", formId));

        // Then
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }

}
