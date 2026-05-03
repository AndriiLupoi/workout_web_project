package org.lupoi.workoutapp.application.usecase.admin;/*
    @author Andrii
    @project workout
    @class GetStatsUseCase
    @version 1.0.0
    @since 03.05.2026 - 12.08
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.lupoi.workoutapp.presentation.dto.response.StatsResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStatsUseCase {

    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;

    public StatsResponse execute() {
        long totalUsers  = userRepository.findAll().size();
        long totalAdmins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();
        long totalPlans  = workoutPlanRepository.countAll();

        return new StatsResponse(totalUsers, totalPlans, totalAdmins);
    }
}
