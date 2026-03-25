package org.lupoi.workoutapp.infrastructure.seed;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.infrastructure.document.ExerciseDocument;
import org.lupoi.workoutapp.infrastructure.repository.MongoExerciseRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExerciseDataSeeder implements ApplicationRunner {

    private final MongoExerciseRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() == 0) {
            repository.saveAll(defaultExercises());
        }
    }

    private List<ExerciseDocument> defaultExercises() {
        return List.of(
                build("Bench Press", "CHEST", "INTERMEDIATE", "BARBELL", "Classic chest press on flat bench"),
                build("Incline Dumbbell Press", "CHEST", "INTERMEDIATE", "DUMBBELL", "Upper chest press on incline bench"),
                build("Push Up", "CHEST", "BEGINNER", "BODYWEIGHT", "Classic bodyweight chest exercise"),
                build("Pull Up", "BACK", "INTERMEDIATE", "BODYWEIGHT", "Vertical pulling movement for back width"),
                build("Barbell Row", "BACK", "INTERMEDIATE", "BARBELL", "Horizontal pulling for back thickness"),
                build("Lat Pulldown", "BACK", "BEGINNER", "MACHINE", "Cable pulldown for back width"),
                build("Squat", "LEGS", "INTERMEDIATE", "BARBELL", "King of all leg exercises"),
                build("Leg Press", "LEGS", "BEGINNER", "MACHINE", "Machine squat alternative"),
                build("Romanian Deadlift", "LEGS", "INTERMEDIATE", "BARBELL", "Hamstring focused hip hinge"),
                build("Overhead Press", "SHOULDERS", "INTERMEDIATE", "BARBELL", "Vertical pressing for shoulder mass"),
                build("Lateral Raise", "SHOULDERS", "BEGINNER", "DUMBBELL", "Side delt isolation"),
                build("Barbell Curl", "BICEPS", "BEGINNER", "BARBELL", "Classic bicep builder"),
                build("Hammer Curl", "BICEPS", "BEGINNER", "DUMBBELL", "Brachialis and bicep curl"),
                build("Tricep Pushdown", "TRICEPS", "BEGINNER", "CABLE", "Cable tricep isolation"),
                build("Skull Crusher", "TRICEPS", "INTERMEDIATE", "BARBELL", "Lying tricep extension"),
                build("Plank", "ABS", "BEGINNER", "BODYWEIGHT", "Core stability exercise"),
                build("Crunch", "ABS", "BEGINNER", "BODYWEIGHT", "Basic ab exercise"),
                build("Treadmill", "CARDIO", "BEGINNER", "MACHINE", "Cardio endurance training")
        );
    }

    private ExerciseDocument build(String name, String muscle,
                                   String difficulty, String equipment, String desc) {
        return ExerciseDocument.builder()
                .name(name)
                .muscleGroup(muscle)
                .difficulty(difficulty)
                .equipmentType(equipment)
                .description(desc)
                .build();
    }
}