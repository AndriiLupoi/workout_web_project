package org.lupoi.workoutapp.integrationTest;/*
    @author Andrii
    @project workout
    @class WorkoutLogIntegrationTest
    @version 1.0.0
    @since 17.05.2026 - 23.51
*/

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lupoi.workoutapp.application.command.LogWorkoutCommand;
import org.lupoi.workoutapp.application.command.RegisterUserCommand;
import org.lupoi.workoutapp.application.command.SaveUserProfileCommand;
import org.lupoi.workoutapp.application.usecase.user.RegisterUserUseCase;
import org.lupoi.workoutapp.application.usecase.user.SaveUserProfileUseCase;
import org.lupoi.workoutapp.application.usecase.workout.logs.LogWorkoutUseCase;
import org.lupoi.workoutapp.application.usecase.workout.logs.GetWorkoutLogsUseCase;
import org.lupoi.workoutapp.application.usecase.workout.plan.GenerateWorkoutPlanUseCase;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.*;
import org.lupoi.workoutapp.domain.model.WorkoutLogResult;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class WorkoutLogIntegrationTest {

    @Autowired private RegisterUserUseCase registerUserUseCase;
    @Autowired private SaveUserProfileUseCase saveUserProfileUseCase;
    @Autowired private GenerateWorkoutPlanUseCase generateWorkoutPlanUseCase;
    @Autowired private LogWorkoutUseCase logWorkoutUseCase;
    @Autowired private GetWorkoutLogsUseCase getWorkoutLogsUseCase;
    @Autowired private UserRepository userRepository;

    private String userId;
    private String planId;

    @BeforeEach
    void setUp() {
        userRepository.findByEmail("log@test.com")
                .ifPresent(u -> userRepository.deleteById(u.getId()));

        User user = registerUserUseCase.execute(
                new RegisterUserCommand("log@test.com", "Password123!", "Log", "User")
        );
        userId = user.getId();

        saveUserProfileUseCase.execute(userId, new SaveUserProfileCommand(
                TrainingGoal.MASS, FitnessLevel.INTERMEDIATE, PlanType.HYPERTROPHY,
                3, 80.0, 75.0, 80.0, 180.0, 25,
                List.of("BARBELL", "DUMBBELL", "BODYWEIGHT")
        ));

        WorkoutPlan plan = generateWorkoutPlanUseCase.execute(userId);
        planId = plan.getId();
    }

    @Test
    @DisplayName("Логування тренування — зберігається і повертається")
    void logWorkout_thenGet_shouldReturnLog() {
        LogWorkoutCommand cmd = new LogWorkoutCommand(
                planId, 1, 1,
                List.of(new LogWorkoutCommand.LoggedExerciseCommand(
                        "ex-1", "Bench Press",
                        3, "8-12", 60.0,
                        3, "10", 65.0,
                        true, "добре"
                )),
                "перше тренування"
        );

        WorkoutLogResult result = logWorkoutUseCase.execute(userId, cmd);
        assertThat(result.logId()).isNotNull();

        List<WorkoutLog> logs = getWorkoutLogsUseCase.executeByPlan(userId, planId);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getDayNumber()).isEqualTo(1);
        assertThat(logs.get(0).getWeekNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("PR визначається при перевищенні попередньої ваги")
    void logWorkout_shouldDetectPR_whenWeightIncreased() {
        LogWorkoutCommand first = new LogWorkoutCommand(
                planId, 1, 1,
                List.of(new LogWorkoutCommand.LoggedExerciseCommand(
                        "ex-1", "Squat", 3, "5", 100.0,
                        3, "5", 100.0, false, ""
                )), ""
        );
        logWorkoutUseCase.execute(userId, first);

        LogWorkoutCommand second = new LogWorkoutCommand(
                planId, 1, 2,
                List.of(new LogWorkoutCommand.LoggedExerciseCommand(
                        "ex-1", "Squat", 3, "5", 100.0,
                        3, "5", 105.0, true, ""
                )), ""
        );
        WorkoutLogResult result = logWorkoutUseCase.execute(userId, second);

        assertThat(result.prCount()).isEqualTo(1);
        assertThat(result.personalRecords().get(0).newWeight()).isEqualTo(105.0);
        assertThat(result.personalRecords().get(0).previousBest()).isEqualTo(100.0);
    }
}
