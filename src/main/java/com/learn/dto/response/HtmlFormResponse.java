package com.learn.dto.response;

import com.learn.constants.FormStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HtmlFormResponse {
    private Long id;
    private String name;
    private String remark;
    private FormStatus formStatus;
}
