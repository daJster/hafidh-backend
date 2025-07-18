package com.hafidh.repository;

import com.hafidh.entity.User_old;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User_old, Long> {
    Optional<User_old> findByEmail(String email);
}