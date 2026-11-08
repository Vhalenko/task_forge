package com.taskforge.model.dto.request;

import com.taskforge.model.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest (
        @NotNull UserRole role) {
}
