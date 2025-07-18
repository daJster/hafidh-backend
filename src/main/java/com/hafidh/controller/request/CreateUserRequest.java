package com.hafidh.controller.request;

import com.hafidh.enums.Role;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateUserRequest(
        @NotNull String username,
        @NotNull String email,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull Role role,
        String password,
        List<Long> classroomIds
) {}
