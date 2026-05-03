package org.lupoi.workoutapp.application.usecase.user;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.SaveUserProfileCommand;
import org.lupoi.workoutapp.domain.entity.BodyWeightLog;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.repository.BodyWeightLogRepository;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SaveUserProfileUseCase {

    private final UserProfileRepository userProfileRepository;
    private final BodyWeightLogRepository bodyWeightLogRepository;

    public UserProfile execute(String userId, SaveUserProfileCommand command) {
        UserProfile existing = userProfileRepository.findByUserId(userId).orElse(null);

        UserProfile profile = UserProfile.builder()
                .id(existing != null ? existing.getId() : null)
                .userId(userId)
                .goal(command.goal())
                .level(command.level())
                .planType(command.planType())
                .workoutsPerWeek(command.workoutsPerWeek())
                .currentWeight(command.currentWeight())
                .targetWeight(command.targetWeight())
                .height(command.height())
                .age(command.age())
                .availableEquipment(command.availableEquipment())
                .build();

        UserProfile saved = userProfileRepository.save(profile);

        if (command.currentWeight() != null && command.currentWeight() > 0) {
            BodyWeightLog log = BodyWeightLog.builder()
                    .userId(userId)
                    .weight(command.currentWeight())
                    .date(LocalDate.now())
                    .build();
            bodyWeightLogRepository.save(log);
        }

        return saved;
    }
}