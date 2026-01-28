package com.assessment.Project.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePasteRequest {

    @NotBlank(message = "content must be a non-empty string")
    private String content;

    @Min(value = 1, message = "ttl_seconds must be >= 1")
    private Integer ttl_seconds;

    @Min(value = 1, message = "max_views must be >= 1")
    private Integer max_views;
}
