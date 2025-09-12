package com.hafidh.repository.user;

import com.hafidh.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByRoleAndActiveTrue(String role, Pageable pageable);

    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.id IN " +
            "(SELECT DISTINCT uc.user.id FROM UserClassroom uc WHERE uc.classroom.id IN :classroomIds)")
    List<User> findByRoleAndClassroomIds(@Param("role") String role, @Param("classroomIds") List<Long> classroomIds);

    @Query("SELECT u FROM User u WHERE u.id IN " +
            "(SELECT DISTINCT uc.user.id FROM UserClassroom uc WHERE uc.classroom.id IN :classroomIds)")
    List<User> findByClassroomIds(@Param("classroomIds") List<Long> classroomIds);

    @Query("SELECT u FROM User u JOIN u.children c WHERE c.id = :parentId")
    List<User> findChildrenByParentId(@Param("parentId") Long parentId);

    @Query("SELECT u FROM User u JOIN u.parents p WHERE p.id = :studentId")
    List<User> findParentsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT u FROM User u WHERE u.active = true AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    org.springframework.data.domain.Page<User> findByKeywordIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
}