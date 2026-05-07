package org.lupoi.workoutapp.application.usecase.workout.plan;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.model.WeightRecommendation;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
    @author Andrii
    @project workout
    @class GetWeightRecommendationUseCase
    @version 1.0.0
    @since 07.05.2026
*/

@Service
@RequiredArgsConstructor
public class GetWeightRecommendationUseCase {

    private final WorkoutLogRepository workoutLogRepository;

    public Optional<WeightRecommendation> execute(
            String userId, String planId, String exerciseId, Double plannedWeight) {

        List<WorkoutLog> logs = workoutLogRepository.findByUserIdAndPlanId(userId, planId);

        for (int i = logs.size() - 1; i >= 0; i--) {
            Optional<org.lupoi.workoutapp.domain.entity.LoggedExercise> found =
                    logs.get(i).getExercises().stream()
                            .filter(e -> e.getExerciseId().equals(exerciseId) && e.getActualWeight() != null)
                            .findFirst();

            if (found.isPresent()) {
                org.lupoi.workoutapp.domain.entity.LoggedExercise ex = found.get();
                double prevWeight = ex.getActualWeight();
                Boolean feltEasy = ex.isFelt_easy();

                if (Boolean.TRUE.equals(feltEasy)) {
                    double w = round(prevWeight + 2.5);
                    return Optional.of(new WeightRecommendation(w, w + " кг (+2.5)", "було легко"));
                }
                if (Boolean.FALSE.equals(feltEasy)) {
                    double w = Math.max(0, round(prevWeight - 2.5));
                    return Optional.of(new WeightRecommendation(w, w + " кг (-2.5)", "було важко"));
                }
                double w = round(prevWeight + 2.5);
                return Optional.of(new WeightRecommendation(w, w + " кг (+2.5)", "оптимістично"));
            }
        }

        if (plannedWeight != null && plannedWeight > 0) {
            double w = round(plannedWeight + 2.5);
            return Optional.of(new WeightRecommendation(w, w + " кг (+2.5)", "перше тренування"));
        }

        return Optional.empty();
    }

    private double round(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}