package com.learn.mapper;

import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.HtmlForm;
import org.mapstruct.Mapper;

@Mapper
public abstract class HtmlFormMapper {
    public abstract HtmlForm fromCreationPayload(HtmlFormCreationPayload payload);
    public abstract HtmlFormResponse fromHtmlForm(HtmlForm htmlForm);
}
