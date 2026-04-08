// SaveUserProfileUseCase.java
package org.lupoi.workoutapp.application.usecase;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.SaveUserProfileCommand;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveUserProfileUseCase {

    private final UserProfileRepository userProfileRepository;

    public UserProfile execute(String userId, SaveUserProfileCommand command) {
        UserProfile existing = userProfileRepository.findByUserId(userId)
                .orElse(null);

        UserProfile profile = UserProfile.builder()
                .id(existing != null ? existing.getId() : null)
                .userId(userId)
                .goal(command.goal())
                .level(command.level())
                .workoutsPerWeek(command.workoutsPerWeek())
                .currentWeight(command.currentWeight())
                .targetWeight(command.targetWeight())
                .height(command.height())
                .age(command.age())
                .availableEquipment(command.availableEquipment())
                .build();

        return userProfileRepository.save(profile);
    }
}