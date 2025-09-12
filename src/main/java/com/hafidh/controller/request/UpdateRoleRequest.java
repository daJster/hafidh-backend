package com.hafidh.controller.request;

import com.hafidh.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull String role) {}
