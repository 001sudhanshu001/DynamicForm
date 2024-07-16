package com.learn.mapper;

import com.learn.dto.request.HtmlFormCreationPayload;
import com.learn.dto.response.HtmlFormResponse;
import com.learn.entity.HtmlForm;
import org.springframework.stereotype.Component;

@Component
public class HtmlFormMapperImpl extends HtmlFormMapper {

    @Override
    public HtmlForm fromCreationPayload(HtmlFormCreationPayload payload) {
        if ( payload == null ) {
            return null;
        }

        HtmlForm htmlForm = new HtmlForm();

        htmlForm.setName(payload.getName());
        htmlForm.setRemark(payload.getRemark());

        return htmlForm;
    }

    @Override
    public HtmlFormResponse fromHtmlForm(HtmlForm htmlForm) {
        if (htmlForm == null) {
            return null;
        }

        HtmlFormResponse htmlFormResponse = new HtmlFormResponse();

        htmlFormResponse.setId(htmlForm.getId());
        htmlFormResponse.setName(htmlForm.getName());
        htmlFormResponse.setRemark(htmlForm.getRemark());
        htmlFormResponse.setFormStatus(htmlForm.getFormStatus());

        return htmlFormResponse;
    }
}
