package com.hafidh.service;

import com.hafidh.controller.request.CreateUserRequest;
import com.hafidh.dto.classroom.ClassroomDTO;
import com.hafidh.dto.task.TaskDTO;
import com.hafidh.dto.user.UserActivityDTO;
import com.hafidh.dto.user.UserDTO;
import com.hafidh.dto.user.UserPreferencesDTO;
import com.hafidh.dto.user.UserProfileDTO;
import com.hafidh.entity.classroom.Classroom;
import com.hafidh.entity.task.Task;
import com.hafidh.entity.user.UserActivity;
import com.hafidh.entity.user.UserPreferences;
import com.hafidh.enums.Role;
import com.hafidh.entity.user.User;
import com.hafidh.exception.ResourceNotFoundException;
import com.hafidh.mapper.classroom.ClassroomMapper;
import com.hafidh.mapper.user.UserActivityMapper;
import com.hafidh.mapper.user.UserMapper;
import com.hafidh.mapper.user.UserPreferencesMapper;
import com.hafidh.repository.user.UserActivityRepository;
import com.hafidh.repository.user.UserPreferencesRepository;
import com.hafidh.repository.user.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final UserPreferencesMapper userPreferencesMapper;
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final UserActivityRepository userActivityRepository;
    private final UserActivityMapper userActivityMapper;
    private final FileStorageService fileStorageService;
    private final ClassroomMapper classroomMapper;
    private final ClassroomRepository classroomRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserPreferencesRepository userPreferencesRepository,
                       UserActivityRepository userActivityRepository, UserMapper userMapper,
                       UserPreferencesMapper userPreferencesMapper, ClassroomMapper classroomMapper,
                       UserActivityMapper userActivityMapper) {
        this.userRepository = userRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.userActivityRepository = userActivityRepository;
        this.userMapper = userMapper;
        this.userPreferencesMapper = userPreferencesMapper;
        this.classroomMapper = classroomMapper;
        this.userActivityMapper  = userActivityMapper;
    }

    // Core user operations
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable, String role, String search, Boolean active) {
        Specification<User> spec = Specification.where(null);

        if (role != null && !role.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), role));
        }

        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("username")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("email")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("firstName")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("lastName")), "%" + search.toLowerCase() + "%")
                    )
            );
        }

        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }

        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

    public Boolean createUser(CreateUserRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Create default preferences
        UserPreferences preferences = UserPreferences.builder()
                .user(savedUser)
                .theme("light")
                .language("en")
                .build();
        userPreferencesRepository.save(preferences);

        logUserActivity(savedUser.getId(), "USER_CREATED", "User account created");

        return true;
    }

    public UserDTO updateUserProfile(Long id, UserProfileDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        logUserActivity(id, "PROFILE_UPDATED", "User profile updated");

        return userMapper.toDto(updatedUser);
    }

    public UserDTO partialUpdateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    user.setFirstName((String) value);
                    break;
                case "lastName":
                    user.setLastName((String) value);
                    break;
                case "email":
                    user.setEmail((String) value);
                    break;
                case "phone":
                    user.setPhone((String) value);
                    break;
                case "active":
                    user.setActive((Boolean) value);
                    break;
                default:
                    log.warn("Unknown field for partial update: {}", key);
            }
        });

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        logUserActivity(id, "PARTIAL_UPDATE", "User partially updated: " + updates.keySet());

        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Remove user from all classrooms
        user.getClassrooms().clear();

        // Remove all task assignments
        user.getTasks().clear();

        // Delete user preferences
        userPreferencesRepository.deleteByUserId(id);

        userRepository.delete(user);

        logUserActivity(id, "USER_DELETED", "User account deleted");
    }

    public UserDTO toggleUserStatus(Long id, Boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(active);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        String action = active ? "ACTIVATED" : "DEACTIVATED";
        logUserActivity(id, action, "User status changed to: " + (active ? "active" : "inactive"));

        return userMapper.toDto(updatedUser);
    }

    // Role management
    public UserDTO updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Role oldRole = user.getRole();
        user.setRole(Role.valueOf(role));
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        logUserActivity(id, "ROLE_UPDATED", "Role changed from " + oldRole + " to " + role);

        return userMapper.toDto(updatedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByRole(String role, Pageable pageable) {
        return userRepository.findByRoleAndActiveTrue(role, pageable)
                .map(userMapper::toDto);
    }

    // Classroom relationships
    @Transactional(readOnly = true)
    public List<ClassroomDTO> getUserClassrooms(Long id, Boolean includeArchived) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (includeArchived == null || includeArchived) {
            return user.getClassrooms()
                    .stream()
                    .map(classroomMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return user.getClassrooms().stream()
                    .filter(classroom -> !classroom.isArchived())
                    .collect(Collectors.toList());
        }
    }

    public void addUserToClassroom(Long id, Long classroomId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));

        if (!user.getClassrooms().contains(classroom)) {
            user.getClassrooms().add(classroom);
            userRepository.save(user);

            logUserActivity(id, "CLASSROOM_JOINED", "Added to classroom: " + classroom.getName());
        }
    }

    public void removeUserFromClassroom(Long id, Long classroomId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id: " + classroomId));

        if (user.getClassrooms().contains(classroom)) {
            user.getClassrooms().remove(classroom);
            userRepository.save(user);

            logUserActivity(id, "CLASSROOM_LEFT", "Removed from classroom: " + classroom.getName());
        }
    }

    // Task relationships
    @Transactional(readOnly = true)
    public Page<TaskDTO> getUserTasks(Long id, Pageable pageable, String status, Long classroomId) {
        if (status != null && classroomId != null) {
            return taskRepository.findByUserIdAndStatusAndClassroomId(id, status, classroomId, pageable);
        } else if (status != null) {
            return taskRepository.findByUserIdAndStatus(id, status, pageable);
        } else if (classroomId != null) {
            return taskRepository.findByUserIdAndClassroomId(id, classroomId, pageable);
        } else {
            return taskRepository.findByUserId(id, pageable);
        }
    }

    public void assignTaskToUser(Long id, Long taskId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (!user.getTasks().contains(task)) {
            user.getTasks().add(task);
            userRepository.save(user);

            logUserActivity(id, "TASK_ASSIGNED", "Task assigned: " + task.getTitle());
        }
    }

    public void removeTaskFromUser(Long id, Long taskId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (user.getTasks().contains(task)) {
            user.getTasks().remove(task);
            userRepository.save(user);

            logUserActivity(id, "TASK_REMOVED", "Task removed: " + task.getTitle());
        }
    }

    // Parent-child relationships
    @Transactional(readOnly = true)
    public List<User> getParentChildren(Long id) {
        return userRepository.findChildrenByParentId(id);
    }

    @Transactional(readOnly = true)
    public List<User> getStudentParents(Long id) {
        return userRepository.findParentsByStudentId(id);
    }

    // User preferences and profile
    public UserPreferencesDTO updateUserPreferences(Long id, @Valid UserPreferencesDTO preferences) {
        UserPreferences existing = userPreferencesRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User preferences not found for user id: " + id));

        existing.setTheme(preferences.getTheme());
        existing.setLanguage(preferences.getLanguage());
        existing.setEmailNotifications(preferences.getEmailNotifications());
        existing.setUpdatedAt(LocalDateTime.now());

        UserPreferences updated = userPreferencesRepository.save(existing);

        logUserActivity(id, "PREFERENCES_UPDATED", "User preferences updated");

        return userPreferencesMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    public UserPreferencesDTO getUserPreferences(Long id) {
        UserPreferences userPreferences = userPreferencesRepository.findByUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User preferences not found for user id: " + id));


        return userPreferencesMapper.toDto(userPreferences);
    }

    public String updateUserAvatar(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String avatarUrl = fileStorageService.storeFile(file, "avatars");
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        logUserActivity(id, "AVATAR_UPDATED", "User avatar updated");

        return avatarUrl;
    }

    // Export functionality
    @Transactional(readOnly = true)
    public byte[] exportUsersToCSV(String role, List<Long> classroomIds) {
        List<User> users;

        if (role != null && classroomIds != null && !classroomIds.isEmpty()) {
            users = userRepository.findByRoleAndClassroomIds(role, classroomIds);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else if (classroomIds != null && !classroomIds.isEmpty()) {
            users = userRepository.findByClassroomIds(classroomIds);
        } else {
            users = userRepository.findAll();
        }

        return generateCSV(users);
    }

    // Activity tracking
    @Transactional(readOnly = true)
    public Page<UserActivityDTO> getUserActivity(Long id, Pageable pageable) {
        return userActivityRepository.findByUserIdOrderByCreatedAtDesc(id, pageable).map(userActivityMapper::toDto);
    }

    private void logUserActivity(Long userId, String action, String description) {
        UserActivity activity = UserActivity.builder()
                .id(userId)
                .activity(action)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
        userActivityRepository.save(activity);
    }

    private byte[] generateCSV(List<User> users) {
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("ID,Username,Email,First Name,Last Name,Role,Active,Created At\n");

        // Data rows
        for (User user : users) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%s,%s%n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole(),
                    user.getActive(),
                    user.getCreatedAt()
            ));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
        }
}