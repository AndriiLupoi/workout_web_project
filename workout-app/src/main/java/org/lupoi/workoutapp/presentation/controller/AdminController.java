package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class AdminController
    @version 1.0.0
    @since 03.05.2026 - 11.08
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.admin.GetStatsUseCase;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.lupoi.workoutapp.presentation.dto.response.ProfileResponse;
import org.lupoi.workoutapp.presentation.dto.response.StatsResponse;
import org.lupoi.workoutapp.presentation.dto.response.UserResponse;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutPlanResponse;
import org.lupoi.workoutapp.presentation.mapper.ProfileDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.UserDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.WorkoutPlanDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final ProfileDtoMapper profileDtoMapper;
    private final WorkoutPlanDtoMapper workoutPlanDtoMapper;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final GetStatsUseCase getStatsUseCase;
    private final UserProfileRepository userProfileRepository;


    // GET /api/v1/admin/users — список всіх юзерів (ADMIN + OWNER)
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        var users = userRepository.findAll()
                .stream()
                .map(userDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(getStatsUseCase.execute());
    }

    @GetMapping("/users/{userId}/profile")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable String userId) {
        return userProfileRepository.findByUserId(userId)
                .map(profileDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/v1/admin/users/{userId}/plans — плани юзера (тільки OWNER)
    @GetMapping("/users/{userId}/plans")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<WorkoutPlanResponse>> getUserPlans(@PathVariable String userId) {
        var plans = workoutPlanRepository.findByUserId(userId)
                .stream()
                .map(workoutPlanDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(plans);
    }



    // PUT /api/v1/admin/roles/{userId}?role=ADMIN — призначити роль (тільки OWNER)
    @PutMapping("/roles/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable String userId,
            @RequestParam Role role) {

        // OWNER не можна понизити через API — захист
        if (role == Role.OWNER) {
            return ResponseEntity.badRequest().build();
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Не можна змінити роль OWNER
        if (user.getRole() == Role.OWNER) {
            return ResponseEntity.status(403).build();
        }

        var updated = userRepository.updateRole(userId, role);
        return ResponseEntity.ok(userDtoMapper.toResponse(updated));
    }

    // DELETE /api/v1/admin/users/{userId} — видалити юзера (тільки OWNER)
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/plans/{planId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUserPlan(
            @PathVariable String userId,
            @PathVariable String planId) {
        workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        workoutPlanRepository.deleteById(planId);
        return ResponseEntity.noContent().build();
    }

}

