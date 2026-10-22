package com.taskforge.model.dto;

import com.taskforge.model.enums.UserRole;
import lombok.Data;


@Data
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String role;
}
