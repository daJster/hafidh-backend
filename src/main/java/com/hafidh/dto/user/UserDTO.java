package com.hafidh.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hafidh.dto.classroom.ClassroomSummaryDTO;
import com.hafidh.dto.task.TaskSummaryDTO;
import com.hafidh.enums.Role;
import jakarta.validation.constraints.*;
import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.IntSet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserDTO {
    Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters; numbers; and underscores")
    String username;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1;14}$", message = "Invalid phone number format")
    String phone;

    @NotNull(message = "Role is required")
    Role role;

    Boolean active;
    Boolean emailVerified;
    LocalDateTime lastLoginAt;

    // Profile Information
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    String bio;

    String avatarUrl;

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    String title;

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    String department;

    @Size(max = 20, message = "Student ID cannot exceed 20 characters")
    String studentId;

    @Size(max = 20, message = "Employee ID cannot exceed 20 characters")
    String employeeId;

    // Address Information
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    String state;

    @Size(max = 20, message = "Zip code cannot exceed 20 characters")
    String zipCode;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    String country;

    // Emergency Contact
    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    String emergencyContactName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1;14}$", message = "Invalid emergency contact phone format")
    String emergencyContactPhone;

    @Size(max = 100, message = "Emergency contact relation cannot exceed 100 characters")
    String emergencyContactRelation;

    // Role-specific fields
    @Min(value = 1, message = "Grade level must be at least 1")
    @Max(value = 12, message = "Grade level cannot exceed 12")
    Integer gradeLevel;

    LocalDateTime enrollmentDate;
    LocalDateTime graduationDate;
    LocalDateTime hireDate;

    Set<String> subjects;
    Set<String> qualifications;

    // Relationships (summary info)
    List<ClassroomSummaryDTO> classrooms;
    List<TaskSummaryDTO> tasks;
    List<UserSummaryDTO> children;
    List<UserSummaryDTO> parents;

    // Metadata
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // Computed fields
    String fullName;
    Integer unreadNotifications;
    Integer activeTasks;
    Integer activeClassrooms;
}