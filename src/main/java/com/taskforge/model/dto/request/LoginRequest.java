package com.taskforge.model.dto.request;


public record LoginRequest (
        String email,
        String password) {
}
