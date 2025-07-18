package com.hafidh.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import com.hafidh.entity.user.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String message;

    @Column(length = 50)
    private String type; // INFO, WARNING, ERROR, SUCCESS

    @Builder.Default
    private Boolean read = false;

    @Column(length = 255)
    private String actionUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime readAt;
}
