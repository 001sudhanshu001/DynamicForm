package com.learn.repository.custom;

import com.learn.dto.dynamicfilter.AppliedDynamicFilter;
import com.learn.dto.response.FilledHtmlFormResponse;

import java.util.List;

public interface CustomFilledHtmlFormRepository {
    List<FilledHtmlFormResponse> filterFilledForms(AppliedDynamicFilter appliedDynamicFilter);
}
