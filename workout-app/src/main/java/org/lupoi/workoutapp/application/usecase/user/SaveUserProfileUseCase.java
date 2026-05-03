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

        // Зберігаємо вагу тіла якщо вона змінилась або новий запис на сьогодні
        if (command.currentWeight() != null && command.currentWeight() > 0) {
            LocalDate today = LocalDate.now();
            boolean alreadyLoggedToday = bodyWeightLogRepository
                    .findByUserIdAndDate(userId, today)
                    .isPresent();

            // Якщо сьогодні ще не логували — додаємо новий запис
            // Якщо логували але вага змінилась — оновлюємо
            bodyWeightLogRepository.findByUserIdAndDate(userId, today)
                    .ifPresentOrElse(
                            existingLog -> {
                                // оновлюємо якщо вага інша
                                if (!existingLog.getWeight().equals(command.currentWeight())) {
                                    BodyWeightLog updated = BodyWeightLog.builder()
                                            .id(existingLog.getId())
                                            .userId(userId)
                                            .weight(command.currentWeight())
                                            .date(today)
                                            .build();
                                    bodyWeightLogRepository.save(updated);
                                }
                            },
                            () -> {
                                // новий запис
                                BodyWeightLog log = BodyWeightLog.builder()
                                        .userId(userId)
                                        .weight(command.currentWeight())
                                        .date(today)
                                        .build();
                                bodyWeightLogRepository.save(log);
                            }
                    );
        }

        return saved;
    }
}