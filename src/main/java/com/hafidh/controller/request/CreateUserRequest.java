package com.hafidh.controller.request;

import com.hafidh.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateUserRequest {
    @NotNull String username;
    @NotNull String email;
    @NotNull String firstName;
    @NotNull String lastName;
    @NotNull Role role;
    String password;
    List<Long> classroomIds;
}
