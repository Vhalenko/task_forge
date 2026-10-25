package com.taskforge.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserStatusRequest {
    private final boolean isBlocked;
}
