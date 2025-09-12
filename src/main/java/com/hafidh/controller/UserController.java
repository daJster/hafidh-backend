package com.hafidh.controller;

import com.hafidh.controller.request.BulkUpdateUserRequest;
import com.hafidh.controller.request.CreateUserRequest;
import com.hafidh.controller.request.UpdateRoleRequest;
import com.hafidh.controller.request.UserSearchRequest;
import com.hafidh.dto.*;
import com.hafidh.dto.classroom.ClassroomDTO;
import com.hafidh.dto.task.TaskDTO;
import com.hafidh.dto.user.UserActivityDTO;
import com.hafidh.dto.user.UserDTO;
import com.hafidh.dto.user.UserPreferencesDTO;
import com.hafidh.dto.user.UserProfileDTO;
import com.hafidh.entity.user.UserActivity;
import com.hafidh.enums.Role;
import com.hafidh.service.UserService;
import com.hafidh.shared.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UserController {

    @Autowired private UserService userService;

    // ==================== CRUD ====================

    /**
     * Get user by ID with role-based access control
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(
            @PathVariable @Positive Long id,
            Principal principal) {

        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<UserDTO>(true, "User fetched successfully", user));
    }

    /**
     * Get current authenticated user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(Authentication authentication) {
        UserDTO userProfile = userService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Current user profile retrieved successfully", userProfile)
        );
    }

    /**
     * Get all users with pagination and filtering (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<UserDTO> users = userService.getAllUsers(pageable, String.valueOf(role), search, active);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users retrieved successfully", users)
        );
    }

    /**
     * Create new user (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> createUser(@Valid @RequestBody CreateUserRequest request) {
        boolean resp = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserProfileDTO dto,
            Principal principal) {

        UserDTO updatedUser = userService.updateUserProfile(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User created successfully", resp));
    }

    /**
     * Partially update user (PATCH)
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<UserDTO>> partialUpdateUser(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, Object> updates,
            Principal principal) {

        UserDTO updatedUser = userService.partialUpdateUser(id, updates);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User partially updated successfully", updatedUser)
        );
    }

    /**
     * Soft delete user (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Null>> deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", null)
        );
    }

    /**
     * Activate/Deactivate user (Admin only)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> toggleUserStatus(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, Boolean> statusRequest) {

        boolean active = statusRequest.get("active");
        UserDTO updatedUser = userService.toggleUserStatus(id, active);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User status updated successfully", updatedUser)
        );
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * Update user role (Admin only)
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateRoleRequest request) {

        UserDTO updatedUser = userService.updateUserRole(id, request.role());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User role updated successfully", updatedUser)
        );
    }

    /**
     * Get users by role
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsersByRole(
            @PathVariable @Valid Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.getUsersByRole(String.valueOf(role), pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users by role retrieved successfully", users)
        );
    }

    // ==================== CLASSROOM MANAGEMENT ====================

    /**
     * Get user's classrooms
     */
    @GetMapping("/{id}/classrooms")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<List<ClassroomDTO>>> getUserClassrooms(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "false") boolean includeArchived) {

        List<ClassroomDTO> classrooms = userService.getUserClassrooms(id, includeArchived);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User classrooms retrieved successfully", classrooms)
        );
    }

    /**
     * Add user to classroom
     */
    @PostMapping("/{id}/classrooms/{classroomId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Null>> addUserToClassroom(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long classroomId) {

        userService.addUserToClassroom(id, classroomId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User added to classroom successfully", null)
        );
    }

    /**
     * Remove user from classroom
     */
    @DeleteMapping("/{id}/classrooms/{classroomId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Null>> removeUserFromClassroom(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long classroomId) {

        userService.removeUserFromClassroom(id, classroomId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User removed from classroom successfully", null)
        );
    }

    // ==================== TASK MANAGEMENT ====================

    /**
     * Get user's tasks
     */
    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<Page<TaskDTO>>> getUserTasks(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long classroomId,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<TaskDTO> tasks = userService.getUserTasks(id, pageable, status, classroomId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User tasks retrieved successfully", tasks)
        );
    }

    /**
     * Assign task to user
     */
    @PostMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Null>> assignTaskToUser(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long taskId) {

        userService.assignTaskToUser(id, taskId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task assigned to user successfully", null)
        );
    }

    /**
     * Remove task from user
     */
    @DeleteMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Null>> removeTaskFromUser(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long taskId) {

        userService.removeTaskFromUser(id, taskId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task removed from user successfully", null)
        );
    }

    // ==================== ACCOUNT METADATA ====================

    /**
     * Update user preferences
     */
    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<UserPreferencesDTO>> updateUserPreferences(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserPreferencesDTO preferences) {

        UserPreferencesDTO updatedPreferences = userService.updateUserPreferences(id, preferences);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User preferences updated successfully", updatedPreferences)
        );
    }

    /**
     * Get user preferences
     */
    @GetMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<UserPreferencesDTO>> getUserPreferences(@PathVariable @Positive Long id) {
        UserPreferencesDTO preferences = userService.getUserPreferences(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User preferences retrieved successfully", preferences)
        );
    }

    /**
     * Update user avatar
     */
    @PostMapping("/{id}/avatar")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateUserAvatar(
            @PathVariable @Positive Long id,
            @RequestParam("file") MultipartFile file) {

        var avatarUrl = userService.updateUserAvatar(id, file);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User avatar updated successfully", Map.of("avatarUrl", avatarUrl))
        );
    }

    /**
     * Export users to CSV (Admin only)
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<byte[]>> exportUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) List<Long> classroomIds) {

        byte[] csvData = userService.exportUsersToCSV(String.valueOf(role), classroomIds);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Users exported to CSV successfully", csvData)
        );
    }


    /**
     * Get user activity log (Admin only)
     */
    @GetMapping("/{id}/activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserActivityDTO>>> getUserActivity(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<UserActivityDTO> activities = userService.getUserActivity(id, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User activity log retrieved successfully", activities)
        );

    }
}