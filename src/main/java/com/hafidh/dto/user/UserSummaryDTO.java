package com.hafidh.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hafidh.enums.Role;
import lombok.Builder;

/**
 * Summary DTO for user relationships and references
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserSummaryDTO(
        Long id,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String email,
        Role role,
        String avatarUrl,
        Boolean active
) {}
