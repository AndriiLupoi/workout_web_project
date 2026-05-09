package org.lupoi.workoutapp.application.strategy;/*
    @author Andrii
    @project workout
    @class PlanStrategyFactory
    @version 1.0.0
    @since 09.05.2026 - 18.43
*/

import org.lupoi.workoutapp.domain.entity.user.UserProfile;
import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.PlanType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;


import java.util.List;
import java.util.Map;

@Primary
@Component
public class PlanStrategyFactory implements PlanGenerationStrategy {

    private final Map<String, PlanGenerationStrategy> strategies;

    public PlanStrategyFactory(
            @Qualifier("HYPERTROPHY")          PlanGenerationStrategy hypertrophy,
            @Qualifier("STRENGTH")             PlanGenerationStrategy strength,
            @Qualifier("STRENGTH_HYPERTROPHY") PlanGenerationStrategy strengthHypertrophy,
            @Qualifier("FAT_LOSS")             PlanGenerationStrategy fatLoss,
            @Qualifier("ENDURANCE")            PlanGenerationStrategy endurance
    ) {
        this.strategies = Map.of(
                PlanType.HYPERTROPHY.name(),           hypertrophy,
                PlanType.STRENGTH.name(),              strength,
                PlanType.STRENGTH_HYPERTROPHY.name(),  strengthHypertrophy,
                PlanType.FAT_LOSS.name(),              fatLoss,
                PlanType.ENDURANCE.name(),             endurance
        );
    }

    @Override
    public WorkoutPlan generate(UserProfile profile, List<Exercise> exercises) {
        PlanType planType = profile.getPlanType() != null
                ? profile.getPlanType()
                : PlanType.HYPERTROPHY;

        PlanGenerationStrategy strategy = strategies.get(planType.name());

        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for plan type: " + planType);
        }

        return strategy.generate(profile, exercises);
    }
}

