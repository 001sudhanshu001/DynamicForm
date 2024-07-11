package com.learn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HtmlFormCreationPayload {

    @NotBlank
    private String name;

    @NotBlank
    private String remark;

}
