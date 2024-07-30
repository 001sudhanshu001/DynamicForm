package com.learn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDisplayNamePayload {

    @NotNull(message = "Required Form Id")
    private Long formId;

    @NotBlank(message = "Required Form Field Name")
    private String fieldNameToChangeDisplayName;

    @NotBlank(message = "Required New Display Name For Field")
    private String newDisplayNameForField;

}
