package org.lupoi.workoutapp.application.usecase.workout.progress;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.logs.BodyWeightLog;
import org.lupoi.workoutapp.domain.exception.ProfileNotFoundException;
import org.lupoi.workoutapp.domain.model.WeightProgressResult;
import org.lupoi.workoutapp.domain.repository.BodyWeightLogRepository;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    @author Andrii
    @project workout
    @class GetWeightProgressUseCase
    @version 1.0.0
    @since 07.05.2026
*/

@Service
@RequiredArgsConstructor
public class GetWeightProgressUseCase {

    private final UserProfileRepository userProfileRepository;
    private final BodyWeightLogRepository bodyWeightLogRepository;

    public WeightProgressResult execute(String userId) {
        var profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        if (profile.getCurrentWeight() == null || profile.getTargetWeight() == null) {
            return new WeightProgressResult(0, 0, 0, "Вага не вказана");
        }

        List<BodyWeightLog> logs = bodyWeightLogRepository.findByUserId(userId);
        double currentWeight = logs.isEmpty()
                ? profile.getCurrentWeight()
                : logs.get(logs.size() - 1).getWeight();

        double targetWeight = profile.getTargetWeight();
        double startWeight = logs.isEmpty()
                ? profile.getCurrentWeight()
                : logs.get(0).getWeight();

        int percent;
        String message;

        if (Math.abs(currentWeight - targetWeight) < 0.01) {
            percent = 100;
            message = "✓ Ціль досягнута!";
        } else if (targetWeight < startWeight) {
            double totalToLose = startWeight - targetWeight;
            double alreadyLost = Math.max(0, startWeight - currentWeight);
            percent = (int) Math.min(100, (alreadyLost / totalToLose) * 100);
            message = String.format("Ще %.1f кг до цілі", currentWeight - targetWeight);
        } else {
            double totalToGain = targetWeight - startWeight;
            double alreadyGained = Math.max(0, currentWeight - startWeight);
            percent = (int) Math.min(100, (alreadyGained / totalToGain) * 100);
            message = String.format("Ще %.1f кг до набору", targetWeight - currentWeight);
        }

        return new WeightProgressResult(currentWeight, targetWeight, percent, message);
    }
}
