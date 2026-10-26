package com.taskforge.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest (
        @NotNull boolean isBlocked) {
}
