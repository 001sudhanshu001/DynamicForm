package com.learn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HtmlFormControllerTests.class)
public class HtmlFormControllerTests {
    private static final String END_POINT_PATH = "";
    private static final String REQUEST_CONTENT_TYPE = "application/json";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
}
