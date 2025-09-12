package com.hafidh.entity.user;

import com.hafidh.entity.user.UserActivity;
import com.hafidh.entity.user.UserPreferences;
import com.hafidh.entity.classroom.Classroom;
import com.hafidh.entity.task.Task;
import com.hafidh.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@Setter
@Getter
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_role", columnList = "role"),
        @Index(name = "idx_user_active", columnList = "active")
})
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // Basic Information
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, length = 15)
    private String phone;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Account Status
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = (Boolean) true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean emailVerified = (Boolean) false;

    @Column
    private LocalDateTime lastLoginAt;

    @Column(length = 255)
    private String avatarUrl;

    @Column(length = 20)
    private String studentId; // For students

    @Column(length = 20)
    private String employeeId; // For teachers/staff

    // Audit Fields
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt; // For soft delete

    // Relationships
    /**
     * Many-to-Many relationship with Classroom
     * A user can be in multiple classrooms (student in multiple classes, teacher teaching multiple classes)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_classrooms",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "classroom_id")
    )
    @Builder.Default
    private Set<Classroom> classrooms = new HashSet<>();

    /**
     * Many-to-Many relationship with Task
     * A user can have multiple tasks assigned, and a task can be assigned to multiple users
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_tasks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    /**
     * Self-referencing Many-to-Many for Parent-Child relationships
     * Parents can have multiple children, children can have multiple parents
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "parent_child",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    @Builder.Default
    private Set<User> children = new HashSet<>();

    @ManyToMany(mappedBy = "children", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> parents = new HashSet<>();

    /**
     * One-to-One relationship with UserPreferences
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreferences preferences;

    /**
     * One-to-Many relationship with UserActivity for audit logging
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserActivity> activities = new HashSet<>();

    /**
     * One-to-Many relationship with Notification
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Notification> notifications = new HashSet<>();

    // Additional fields for different roles

    /**
     * For students - grade level
     */
    @Column
    private Integer gradeLevel;

    /**
     * For students - enrollment date
     */
    @Column
    private LocalDateTime enrollmentDate;

    /**
     * For students - graduation date
     */
    @Column
    private LocalDateTime graduationDate;

    /**
     * For teachers - hire date
     */
    @Column
    private LocalDateTime hireDate;

    /**
     * For teachers - subjects they can teach
     */
    @ElementCollection
    @CollectionTable(name = "user_subjects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "subject")
    @Builder.Default
    private Set<String> subjects = new HashSet<>();

    /**
     * For teachers - qualifications
     */
    @ElementCollection
    @CollectionTable(name = "user_qualifications", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "qualification")
    @Builder.Default
    private Set<String> qualifications = new HashSet<>();

    // Utility Methods

    /**
     * Get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user is active
     */
    public boolean isActive() {
        return active != null && active && deletedAt == null;
    }

    /**
     * Check if user is soft deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Soft delete the user
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = (Boolean) false;
    }

    /**
     * Restore soft deleted user
     */
    public void restore() {
        this.deletedAt = null;
        this.active = (Boolean) true;
    }

    /**
     * Check if user has specific role
     */
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    /**
     * Check if user is a student
     */
    public boolean isStudent() {
        return role == Role.STUDENT;
    }

    /**
     * Check if user is a teacher
     */
    public boolean isTeacher() {
        return role == Role.TEACHER;
    }

    /**
     * Check if user is a parent
     */
    public boolean isParent() {
        return role == Role.PARENT;
    }

    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /**
     * Add classroom to user
     */
    public void addClassroom(Classroom classroom) {
        classrooms.add(classroom);
        classroom.getUsers().add(this);
    }

    /**
     * Remove classroom from user
     */
    public void removeClassroom(Classroom classroom) {
        classrooms.remove(classroom);
        classroom.getUsers().remove(this);
    }

    /**
     * Add task to user
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.getAssignedUsers().add(this);
    }

    /**
     * Remove task from user
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        task.getAssignedUsers().remove(this);
    }

    /**
     * Add child to parent
     */
    public void addChild(User child) {
        if (this.role == Role.PARENT && child.role == Role.STUDENT) {
            children.add(child);
            child.parents.add(this);
        }
    }

    /**
     * Remove child from parent
     */
    public void removeChild(User child) {
        children.remove(child);
        child.parents.remove(this);
    }

    /**
     * Update last login time
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // Override equals and hashCode to use only ID for entity comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}