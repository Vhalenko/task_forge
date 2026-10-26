package com.taskforge.model.dto;

import com.taskforge.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserDTO {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String email;
    private final UserRole role;
}
