package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class UserProfileRepository
    @version 1.0.0
    @since 24.03.2026 - 14.49
*/


import org.lupoi.workoutapp.domain.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository {
    Optional<UserProfile> findByUserId(String userId);
    UserProfile save(UserProfile profile);
}