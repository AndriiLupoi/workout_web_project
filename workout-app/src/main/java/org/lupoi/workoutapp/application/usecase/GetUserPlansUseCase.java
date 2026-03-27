package org.lupoi.workoutapp.application.usecase;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserPlansUseCase {

    private final WorkoutPlanRepository workoutPlanRepository;

    public List<WorkoutPlan> execute(String userId) {
        return workoutPlanRepository.findByUserId(userId);
    }
}