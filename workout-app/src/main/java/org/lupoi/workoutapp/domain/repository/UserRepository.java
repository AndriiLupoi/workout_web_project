package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class UserRepository
    @version 1.0.0
    @since 24.03.2026 - 14.40
*/

import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    boolean existsByEmail(String email);
    User save(User user);

    List<User> findAll();
    User updateRole(String userId, Role role);
    void deleteById(String userId);

}