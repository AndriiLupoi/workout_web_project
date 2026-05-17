package org.lupoi.workoutapp.integrationTest;/*
    @author Andrii
    @project workout
    @class WorkoutPlanIntegrationTest
    @version 1.0.0
    @since 17.05.2026 - 23.50
*/

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lupoi.workoutapp.application.command.RegisterUserCommand;
import org.lupoi.workoutapp.application.command.SaveUserProfileCommand;
import org.lupoi.workoutapp.application.usecase.user.RegisterUserUseCase;
import org.lupoi.workoutapp.application.usecase.user.SaveUserProfileUseCase;
import org.lupoi.workoutapp.application.usecase.user.GetUserProfileUseCase;
import org.lupoi.workoutapp.application.usecase.workout.plan.GenerateWorkoutPlanUseCase;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.*;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WorkoutPlanIntegrationTest {

    @Autowired private RegisterUserUseCase registerUserUseCase;
    @Autowired private SaveUserProfileUseCase saveUserProfileUseCase;
    @Autowired private GetUserProfileUseCase getUserProfileUseCase;
    @Autowired private GenerateWorkoutPlanUseCase generateWorkoutPlanUseCase;
    @Autowired private UserRepository userRepository;

    private String userId;

    @BeforeEach
    void setUp() {
        userRepository.findByEmail("plan@test.com")
                .ifPresent(u -> userRepository.deleteById(u.getId()));

        User user = registerUserUseCase.execute(
                new RegisterUserCommand("plan@test.com", "Password123!", "Test", "User")
        );
        userId = user.getId();

        SaveUserProfileCommand profile = new SaveUserProfileCommand(
                TrainingGoal.MASS,
                FitnessLevel.INTERMEDIATE,
                PlanType.HYPERTROPHY,
                3,
                80.0, 75.0, 80.0,
                180.0, 25,
                List.of("BARBELL", "DUMBBELL", "BODYWEIGHT")
        );
        saveUserProfileUseCase.execute(userId, profile);
    }

    @Test
    @DisplayName("Генерація плану — повний цикл від профілю до плану")
    void generatePlan_shouldReturnValidPlan() {
        WorkoutPlan plan = generateWorkoutPlanUseCase.execute(userId);

        assertThat(plan).isNotNull();
        assertThat(plan.getId()).isNotNull();
        assertThat(plan.getUserId()).isEqualTo(userId);
        assertThat(plan.getDays()).isNotEmpty();
        assertThat(plan.getDurationWeeks()).isEqualTo(8);
    }

    @Test
    @DisplayName("Кожен день плану містить вправи")
    void generatePlan_everyDayHasExercises() {
        WorkoutPlan plan = generateWorkoutPlanUseCase.execute(userId);

        plan.getDays().forEach(day ->
                assertThat(day.getExercises())
                        .as("День %d тижня %d повинен мати вправи", day.getDayNumber(), day.getWeekNumber())
                        .isNotEmpty()
        );
    }

    @Test
    @DisplayName("Збереження профілю і отримання — дані збігаються")
    void saveProfile_thenGet_shouldReturnSameData() {
        var profile = getUserProfileUseCase.execute(userId);

        assertThat(profile.getGoal()).isEqualTo(TrainingGoal.MASS);
        assertThat(profile.getLevel()).isEqualTo(FitnessLevel.INTERMEDIATE);
        assertThat(profile.getWorkoutsPerWeek()).isEqualTo(3);
        assertThat(profile.getCurrentWeight()).isEqualTo(80.0);
    }
}
