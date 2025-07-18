package com.hafidh.controller.request;

import com.hafidh.enums.Role;
import jakarta.validation.constraints.NotNull;

public record BulkUpdateUserRequest(
        @NotNull Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        Boolean active
) {}