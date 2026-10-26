package com.taskforge.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest (
        @NotBlank String name) {
}
