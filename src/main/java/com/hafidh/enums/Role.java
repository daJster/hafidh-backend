package com.hafidh.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Enum representing different user roles in the educational system
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("Admin", "System Administrator",
            Set.of(
                    Permission.USER_READ, Permission.USER_WRITE, Permission.USER_DELETE,
                    Permission.CLASSROOM_READ, Permission.CLASSROOM_WRITE, Permission.CLASSROOM_DELETE,
                    Permission.TASK_READ, Permission.TASK_WRITE, Permission.TASK_DELETE,
                    Permission.PARENT_READ, Permission.PARENT_WRITE,
                    Permission.STUDENT_READ, Permission.STUDENT_WRITE,
                    Permission.TEACHER_READ, Permission.TEACHER_WRITE,
                    Permission.SYSTEM_CONFIG, Permission.REPORTS_READ, Permission.AUDIT_READ
            ),
            true, 1),

    TEACHER("Teacher", "Educator/Instructor",
            Set.of(
                    Permission.USER_READ, // Can read user profiles
                    Permission.CLASSROOM_READ, Permission.CLASSROOM_WRITE, // Can manage their classrooms
                    Permission.TASK_READ, Permission.TASK_WRITE, // Can create and manage tasks
                    Permission.STUDENT_READ, Permission.STUDENT_WRITE, // Can manage students in their classes
                    Permission.PARENT_READ, // Can view parent information
                    Permission.REPORTS_READ // Can generate reports for their classes
            ),
            true, 2),

    STUDENT("Student", "Student/Learner",
            Set.of(
                    Permission.USER_READ, // Can read own profile
                    Permission.CLASSROOM_READ, // Can view their classrooms
                    Permission.TASK_READ, // Can view assigned tasks
                    Permission.PARENT_READ // Can view their parent information
            ),
            false, 3),

    PARENT("Parent", "Parent/Guardian",
            Set.of(
                    Permission.USER_READ, // Can read own profile
                    Permission.CLASSROOM_READ, // Can view their children's classrooms
                    Permission.TASK_READ, // Can view their children's tasks
                    Permission.STUDENT_READ, // Can view their children's information
                    Permission.TEACHER_READ // Can view their children's teachers
            ),
            false, 4);

    private final String displayName;
    private final String description;
    private final Set<Permission> permissions;
    private final boolean isStaff; // Staff members (admin, teacher) vs non-staff (student, parent)
    private final int hierarchyLevel; // Lower number = higher authority

    /**
     * Check if this role has a specific permission
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * Check if this role has any of the specified permissions
     */
    public boolean hasAnyPermission(Permission... permissions) {
        return Arrays.stream(permissions)
                .anyMatch(this.permissions::contains);
    }

    /**
     * Check if this role has all of the specified permissions
     */
    public boolean hasAllPermissions(Permission... permissions) {
        return Arrays.stream(permissions)
                .allMatch(this.permissions::contains);
    }

    /**
     * Check if this role has higher authority than another role
     */
    public boolean hasHigherAuthorityThan(Role other) {
        return this.hierarchyLevel < other.hierarchyLevel;
    }

    /**
     * Check if this role has lower authority than another role
     */
    public boolean hasLowerAuthorityThan(Role other) {
        return this.hierarchyLevel > other.hierarchyLevel;
    }

    /**
     * Check if this role can manage another role
     */
    public boolean canManage(Role other) {
        return switch (this) {
            case ADMIN -> true; // Admin can manage everyone
            case TEACHER -> other == STUDENT; // Teachers can manage students
            case STUDENT, PARENT -> false; // Students and parents can't manage anyone
        };
    }

    /**
     * Get roles that can be managed by this role
     */
    public Set<Role> getManagedRoles() {
        return switch (this) {
            case ADMIN -> Set.of(ADMIN, TEACHER, STUDENT, PARENT);
            case TEACHER -> Set.of(STUDENT);
            case STUDENT, PARENT -> Set.of();
        };
    }

    /**
     * Check if this is an administrative role
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this is a teaching role
     */
    public boolean isTeacher() {
        return this == TEACHER;
    }

    /**
     * Check if this is a student role
     */
    public boolean isStudent() {
        return this == STUDENT;
    }

    /**
     * Check if this is a parent role
     */
    public boolean isParent() {
        return this == PARENT;
    }

    /**
     * Get all staff roles
     */
    public static List<Role> getStaffRoles() {
        return Arrays.stream(values())
                .filter(role -> role.isStaff)
                .toList();
    }

    /**
     * Get all non-staff roles
     */
    public static List<Role> getNonStaffRoles() {
        return Arrays.stream(values())
                .filter(role -> !role.isStaff)
                .toList();
    }

    /**
     * Get role by display name (case-insensitive)
     */
    public static Role fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(role -> role.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + displayName));
    }

    /**
     * Get role by name (case-insensitive)
     */
    public static Role fromString(String roleName) {
        try {
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + roleName);
        }
    }

    /**
     * Check if a role name is valid
     */
    public static boolean isValidRole(String roleName) {
        try {
            Role.valueOf(roleName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get default role for new users
     */
    public static Role getDefaultRole() {
        return STUDENT;
    }

    /**
     * Get roles that can create classrooms
     */
    public static List<Role> getClassroomCreators() {
        return Arrays.stream(values())
                .filter(role -> role.hasPermission(Permission.CLASSROOM_WRITE))
                .toList();
    }

    /**
     * Get roles that can create tasks
     */
    public static List<Role> getTaskCreators() {
        return Arrays.stream(values())
                .filter(role -> role.hasPermission(Permission.TASK_WRITE))
                .toList();
    }

    /**
     * Get the Spring Security role name (prefixed with ROLE_)
     */
    public String getSpringSecurityRole() {
        return "ROLE_" + this.name();
    }

    @Override
    public String toString() {
        return displayName;
    }
}