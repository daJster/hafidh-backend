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
import com.hafidh.enums.Role;
import com.hafidh.service.UserService;
import jakarta.validation.Valid;
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

    // ==================== USER CRUD OPERATIONS ====================

    /**
     * Get user by ID with role-based access control
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable @Positive Long id,
            Principal principal) {

        var user = userService.getUserByIdd(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get current authenticated user's profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        var userProfile = userService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(userProfile);
    }

    /**
     * Get all users with pagination and filtering (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {

        var sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        var pageable = PageRequest.of(page, size, sort);

        var users = userService.getAllUsers(pageable, role, search, active);
        return ResponseEntity.ok(users);
    }

    /**
     * Create new user (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        var createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserProfileDTO dto,
            Principal principal) {

        var updatedUser = userService.updateUserProfilee(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Partially update user (PATCH)
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<UserDTO> partialUpdateUser(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, Object> updates,
            Principal principal) {

        var updatedUser = userService.partialUpdateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Soft delete user (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activate/Deactivate user (Admin only)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> toggleUserStatus(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, Boolean> statusRequest) {

        boolean active = statusRequest.get("active");
        var updatedUser = userService.toggleUserStatus(id, active);
        return ResponseEntity.ok(updatedUser);
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * Update user role (Admin only)
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateRoleRequest request) {

        var updatedUser = userService.updateUserRole(id, request.role());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Get users by role
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @PathVariable @Valid Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        var pageable = PageRequest.of(page, size);
        var users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    // ==================== CLASSROOM MANAGEMENT ====================

    /**
     * Get user's classrooms
     */
    @GetMapping("/{id}/classrooms")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<List<ClassroomDTO>> getUserClassrooms(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "false") boolean includeArchived) {

        var classrooms = userService.getUserClassrooms(id, includeArchived);
        return ResponseEntity.ok(classrooms);
    }

    /**
     * Add user to classroom
     */
    @PostMapping("/{id}/classrooms/{classroomId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Void> addUserToClassroom(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long classroomId) {

        userService.addUserToClassroom(id, classroomId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove user from classroom
     */
    @DeleteMapping("/{id}/classrooms/{classroomId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Void> removeUserFromClassroom(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long classroomId) {

        userService.removeUserFromClassroom(id, classroomId);
        return ResponseEntity.noContent().build();
    }

    // ==================== TASK MANAGEMENT ====================

    /**
     * Get user's tasks
     */
    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<Page<TaskDTO>> getUserTasks(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long classroomId,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        var sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        var pageable = PageRequest.of(page, size, sort);

        var tasks = userService.getUserTasks(id, pageable, status, classroomId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Assign task to user
     */
    @PostMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Void> assignTaskToUser(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long taskId) {

        userService.assignTaskToUser(id, taskId);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove task from user
     */
    @DeleteMapping("/{id}/tasks/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Void> removeTaskFromUser(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long taskId) {

        userService.removeTaskFromUser(id, taskId);
        return ResponseEntity.noContent().build();
    }

    // ==================== PARENT-STUDENT RELATIONSHIPS ====================

    /**
     * Get parent's children (for parent role)
     */
    @GetMapping("/{id}/children")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARENT') and @userService.canAccessUser(authentication.name, #id))")
    public ResponseEntity<List<UserDTO>> getParentChildren(@PathVariable @Positive Long id) {
        var children = userService.getParentChildren(id);
        return ResponseEntity.ok(children);
    }

    /**
     * Add child to parent
     */
    @PostMapping("/{parentId}/children/{childId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addChildToParent(
            @PathVariable @Positive Long parentId,
            @PathVariable @Positive Long childId) {

        userService.addChildToParent(parentId, childId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get student's parents
     */
    @GetMapping("/{id}/parents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<List<UserDTO>> getStudentParents(@PathVariable @Positive Long id) {
        var parents = userService.getStudentParents(id);
        return ResponseEntity.ok(parents);
    }

    // ==================== ACCOUNT METADATA ====================

    /**
     * Update user preferences
     */
    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<UserPreferencesDTO> updateUserPreferences(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UserPreferencesDTO preferences) {

        var updatedPreferences = userService.updateUserPreferences(id, preferences);
        return ResponseEntity.ok(updatedPreferences);
    }

    /**
     * Get user preferences
     */
    @GetMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<UserPreferencesDTO> getUserPreferences(@PathVariable @Positive Long id) {
        var preferences = userService.getUserPreferences(id);
        return ResponseEntity.ok(preferences);
    }

    /**
     * Update user avatar
     */
    @PostMapping("/{id}/avatar")
    @PreAuthorize("hasRole('ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<Map<String, String>> updateUserAvatar(
            @PathVariable @Positive Long id,
            @RequestParam("file") MultipartFile file) {

        var avatarUrl = userService.updateUserAvatar(id, file);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    // ==================== SEARCH AND FILTERING ====================

    /**
     * Search users by multiple criteria
     */
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @Valid @RequestBody UserSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var pageable = PageRequest.of(page, size);
        var users = userService.searchUsers(searchRequest, pageable);
        return ResponseEntity.ok(users);
    }

//    /**
//     * Get user statistics (Admin only)
//     */
//    @GetMapping("/statistics")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserStatisticsDTO> getUserStatistics() {
//        var statistics = userService.getUserStatistics();
//        return ResponseEntity.ok(statistics);
//    }

    // ==================== BULK OPERATIONS ====================

    /**
     * Bulk create users (Admin only)
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BulkOperationResult> bulkCreateUsers(
            @Valid @RequestBody List<CreateUserRequest> requests) {

        var result = userService.bulkCreateUsers(requests);
        return ResponseEntity.ok(result);
    }

    /**
     * Bulk update users (Admin only)
     */
    @PutMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BulkOperationResult> bulkUpdateUsers(
            @Valid @RequestBody List<BulkUpdateUserRequest> requests) {

        var result = userService.bulkUpdateUsers(requests);
        return ResponseEntity.ok(result);
    }

    /**
     * Export users to CSV (Admin only)
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) List<Long> classroomIds) {

        var csvData = userService.exportUsersToCSV(role, classroomIds);

        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=users.csv")
                .body(csvData);
    }

    // ==================== VALIDATION AND UTILITY ====================

    /**
     * Check if username is available
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(
            @RequestParam String username) {

        var available = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * Check if email is available
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(
            @RequestParam String email) {

        var available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * Get user activity log (Admin only)
     */
    @GetMapping("/{id}/activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserActivityDTO>> getUserActivity(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var pageable = PageRequest.of(page, size);
        var activities = userService.getUserActivity(id, pageable);
        return ResponseEntity.ok(activities);
    }

    // ==================== HELPER METHODS ====================

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserIdByUsername(authentication.getName());
    }

    private boolean isCurrentUserAdmin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}