package com.hafidh.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing specific permissions in the system
 */
@Getter
@RequiredArgsConstructor
public enum Permission {

    // User Management
    USER_READ("Read user information"),
    USER_WRITE("Create and update users"),
    USER_DELETE("Delete users"),

    // Classroom Management
    CLASSROOM_READ("Read classroom information"),
    CLASSROOM_WRITE("Create and update classrooms"),
    CLASSROOM_DELETE("Delete classrooms"),

    // Task Management
    TASK_READ("Read task information"),
    TASK_WRITE("Create and update tasks"),
    TASK_DELETE("Delete tasks"),

    // Student Management
    STUDENT_READ("Read student information"),
    STUDENT_WRITE("Update student information"),

    // Parent Management
    PARENT_READ("Read parent information"),
    PARENT_WRITE("Update parent information"),

    // Teacher Management
    TEACHER_READ("Read teacher information"),
    TEACHER_WRITE("Update teacher information"),

    // System Administration
    SYSTEM_CONFIG("Configure system settings"),
    REPORTS_READ("Generate and view reports"),
    AUDIT_READ("View audit logs");

    private final String description;

    @Override
    public String toString() {
        return description;
    }
}

