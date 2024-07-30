package com.learn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldsDisplayOrderPayload {

    @NotNull(message = "Required Form Id")
    private Long formId;

    @NotNull(message = "Required Field Names")
    @Size(min = 1, message = "At least one field name is required")
    private List<String> fieldNames;

}
