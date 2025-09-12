package com.hafidh.repository.user;

import com.hafidh.entity.user.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {

    Optional<UserPreferences> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT up FROM UserPreferences up WHERE up.user.id = :userId")
    Optional<UserPreferences> findPreferencesByUserId(@Param("userId") Long userId);

    @Query("SELECT up FROM UserPreferences up WHERE up.theme = :theme")
    List<UserPreferences> findByTheme(@Param("theme") String theme);

    @Query("SELECT up FROM UserPreferences up WHERE up.language = :language")
    List<UserPreferences> findByLanguage(@Param("language") String language);

    @Query("SELECT up FROM UserPreferences up WHERE up.notifications = :notifications")
    List<UserPreferences> findByNotifications(@Param("notifications") boolean notifications);
}