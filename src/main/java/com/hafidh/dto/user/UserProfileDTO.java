package com.hafidh.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    private String department;

    // Address Information
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Zip code cannot exceed 20 characters")
    private String zipCode;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    // Emergency Contact
    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid emergency contact phone format")
    private String emergencyContactPhone;

    @Size(max = 100, message = "Emergency contact relation cannot exceed 100 characters")
    private String emergencyContactRelation;

    // Role-specific fields
    @Min(value = 1, message = "Grade level must be at least 1")
    @Max(value = 12, message = "Grade level cannot exceed 12")
    private Integer gradeLevel;

    private LocalDateTime enrollmentDate;
    private LocalDateTime graduationDate;
    private LocalDateTime hireDate;

    private Set<String> subjects;
    private Set<String> qualifications;
}