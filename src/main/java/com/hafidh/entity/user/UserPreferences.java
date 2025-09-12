package com.hafidh.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@Data
@Builder
@Getter
@Setter
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 10)
    private String language = "fr";

    @Column(length = 10)
    private String theme = "light"; // light, dark, system

    @Builder.Default
    private Boolean emailNotifications = true;

    @Builder.Default
    private Boolean pushNotifications = true;

    @Builder.Default
    private Boolean classroomUpdates = true;

    @Builder.Default
    private Boolean parentalNotifications = true;

    @Column(length = 20)
    private String dateFormat = "MM/dd/yyyy";

    @Column(length = 10)
    private String timeFormat = "12h";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

