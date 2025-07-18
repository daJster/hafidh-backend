package com.hafidh.controller.request;

import com.hafidh.enums.Role;

import java.util.List;

public record UserSearchRequest(
        String query,
        Role role,
        List<Long> classroomIds,
        Boolean active,
        String sortBy,
        String sortDirection
) {}
