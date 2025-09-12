package com.hafidh.repository.user;

import com.hafidh.entity.user.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Page<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<UserActivity> findByUserIdAndActionOrderByCreatedAtDesc(Long userId, String action, Pageable pageable);

    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = :userId AND ua.createdAt >= :startDate")
    List<UserActivity> findByUserIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("startDate") java.time.LocalDateTime startDate);

    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = :userId AND ua.createdAt BETWEEN :startDate AND :endDate")
    Page<UserActivity> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                                       @Param("startDate") java.time.LocalDateTime startDate,
                                                       @Param("endDate") java.time.LocalDateTime endDate,
                                                       Pageable pageable);

    @Query("SELECT ua.action, COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId GROUP BY ua.action")
    List<Object[]> countActivitiesByActionForUser(@Param("userId") Long userId);

    @Query("SELECT DATE(ua.createdAt) as activityDate, COUNT(ua) as activityCount " +
            "FROM UserActivity ua WHERE ua.userId = :userId " +
            "GROUP BY DATE(ua.createdAt) ORDER BY activityDate DESC")
    List<Object[]> getDailyActivityCountForUser(@Param("userId") Long userId);

    void deleteByUserId(Long userId);

    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId AND ua.action = :action")
    Long countByUserIdAndAction(@Param("userId") Long userId, @Param("action") String action);
}