package com.taskforge.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthDTO {
    private final String token;
    private final UserDTO user;
}
