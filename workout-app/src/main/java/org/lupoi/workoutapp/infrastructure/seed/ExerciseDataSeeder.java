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
                // ── CHEST ──
                build("Bench Press",              "CHEST", "INTERMEDIATE", "BARBELL",    "Classic chest press on flat bench"),
                build("Incline Dumbbell Press",   "CHEST", "INTERMEDIATE", "DUMBBELL",   "Upper chest press on incline bench"),
                build("Push Up",                  "CHEST", "BEGINNER",     "BODYWEIGHT", "Classic bodyweight chest exercise"),
                build("Chest Fly Machine",        "CHEST", "BEGINNER",     "MACHINE",    "Isolation chest fly (butterfly)"),
                build("Cable Crossover",          "CHEST", "INTERMEDIATE", "CABLE",      "Cable chest fly from different angles"),
                build("Smith Machine Bench Press","CHEST", "INTERMEDIATE", "MACHINE",    "Bench press in smith machine"),
                build("Dips",                     "CHEST", "INTERMEDIATE", "BODYWEIGHT", "Chest-focused dips"),
                build("Hammer Strength Press",    "CHEST", "INTERMEDIATE", "MACHINE",    "Plate-loaded chest press"),
                build("Incline Hammer Press",     "CHEST", "INTERMEDIATE", "MACHINE",    "Incline plate-loaded chest press"),

                // ── BACK ──
                build("Pull Up",                  "BACK", "INTERMEDIATE", "BODYWEIGHT", "Vertical pulling movement for back width"),
                build("Barbell Row",              "BACK", "INTERMEDIATE", "BARBELL",    "Horizontal pulling for back thickness"),
                build("Lat Pulldown",             "BACK", "BEGINNER",     "MACHINE",    "Cable pulldown for back width"),
                build("Seated Cable Row",         "BACK", "BEGINNER",     "CABLE",      "Horizontal cable row"),
                build("T-Bar Row",                "BACK", "INTERMEDIATE", "BARBELL",    "T-bar rowing movement"),
                build("Single Arm Dumbbell Row",  "BACK", "BEGINNER",     "DUMBBELL",   "One arm rowing exercise"),
                build("Pullover",                 "BACK", "INTERMEDIATE", "DUMBBELL",   "Lat isolation pullover"),
                build("Machine Row",              "BACK", "BEGINNER",     "MACHINE",    "Horizontal machine row"),
                build("Wide Grip Lat Pulldown",   "BACK", "BEGINNER",     "MACHINE",    "Wide grip cable pulldown"),

                // ── LEGS ──
                build("Squat",                   "LEGS", "INTERMEDIATE", "BARBELL",    "King of all leg exercises"),
                build("Leg Press",               "LEGS", "BEGINNER",     "MACHINE",    "Machine squat alternative"),
                build("Romanian Deadlift",       "LEGS", "INTERMEDIATE", "BARBELL",    "Hamstring focused hip hinge"),
                build("Leg Extension",           "LEGS", "BEGINNER",     "MACHINE",    "Quadriceps isolation"),
                build("Leg Curl",                "LEGS", "BEGINNER",     "MACHINE",    "Hamstring isolation"),
                build("Lunges",                  "LEGS", "INTERMEDIATE", "DUMBBELL",   "Walking or static lunges"),
                build("Calf Raise",              "LEGS", "BEGINNER",     "MACHINE",    "Calf isolation exercise"),

                // ── SHOULDERS ──
                build("Overhead Press",          "SHOULDERS", "INTERMEDIATE", "BARBELL", "Vertical pressing for shoulder mass"),
                build("Lateral Raise",           "SHOULDERS", "BEGINNER",     "DUMBBELL","Side delt isolation"),
                build("Rear Delt Fly",           "SHOULDERS", "BEGINNER",     "MACHINE", "Rear delt isolation (reverse butterfly)"),
                build("Face Pull",               "SHOULDERS", "BEGINNER",     "CABLE",   "Rear delts and upper back"),
                build("Upright Row",             "SHOULDERS", "INTERMEDIATE", "BARBELL", "Shoulder and traps movement"),
                build("Dumbbell Shoulder Press", "SHOULDERS", "INTERMEDIATE", "DUMBBELL","Seated or standing press"),
                build("Plate Front Raise",       "SHOULDERS", "BEGINNER",     "BODYWEIGHT","Front raise with weight plate"),

                // ── BICEPS ──
                build("Barbell Curl",            "BICEPS", "BEGINNER",     "BARBELL",   "Classic bicep builder"),
                build("Hammer Curl",             "BICEPS", "BEGINNER",     "DUMBBELL",  "Brachialis and bicep curl"),
                build("Preacher Curl",           "BICEPS", "BEGINNER",     "BARBELL",   "Preacher bench curl"),
                build("Cable Curl",              "BICEPS", "BEGINNER",     "CABLE",     "Cable biceps curl"),
                build("Concentration Curl",      "BICEPS", "BEGINNER",     "DUMBBELL",  "Single arm isolation curl"),
                build("Arnold Curl",             "BICEPS", "BEGINNER",     "DUMBBELL",  "Rotating curl variation"),
                build("Cross Body Curl",         "BICEPS", "BEGINNER",     "CABLE",     "Cable curl across body"),
                build("Seated Dumbbell Curl",    "BICEPS", "BEGINNER",     "DUMBBELL",  "Seated bicep curl"),

                // ── TRICEPS ──
                build("Tricep Pushdown",               "TRICEPS", "BEGINNER",     "CABLE",   "Cable tricep isolation"),
                build("Skull Crusher",                 "TRICEPS", "INTERMEDIATE", "BARBELL", "Lying tricep extension"),
                build("Overhead Tricep Extension",     "TRICEPS", "BEGINNER",     "DUMBBELL","Overhead extension"),
                build("Cable Overhead Extension",      "TRICEPS", "BEGINNER",     "CABLE",   "Cable overhead triceps"),
                build("Close Grip Bench Press",        "TRICEPS", "INTERMEDIATE", "BARBELL", "Triceps focused bench press"),
                build("French Press",                  "TRICEPS", "INTERMEDIATE", "BARBELL", "Lying skull crusher with EZ bar"),
                build("Single Arm Overhead Extension", "TRICEPS", "BEGINNER",     "DUMBBELL","One arm overhead extension"),
                build("Machine Tricep Extension",      "TRICEPS", "BEGINNER",     "MACHINE", "Machine tricep isolation"),

                // ── FOREARMS ──
                build("Reverse Curl",            "FOREARMS", "BEGINNER", "BARBELL",  "Reverse grip bicep curl"),
                build("Wrist Curl",              "FOREARMS", "BEGINNER", "DUMBBELL", "Forearm flexion isolation"),

                // ── TRAPS ──
                build("Barbell Shrug",        "TRAPS", "BEGINNER",     "BARBELL",  "Classic barbell shrug"),
                build("Dumbbell Shrug",       "TRAPS", "BEGINNER",     "DUMBBELL", "Dumbbell trap isolation"),
                build("Smith Machine Shrug",  "TRAPS", "BEGINNER",     "MACHINE",  "Shrugs in smith machine"),
                build("Behind The Back Shrug","TRAPS", "INTERMEDIATE", "BARBELL",  "Shrugs behind the body"),
                build("Farmer's Walk",        "TRAPS", "INTERMEDIATE", "DUMBBELL", "Heavy carry for traps and grip"),
                build("Rack Pull",            "TRAPS", "ADVANCED",     "BARBELL",  "Partial deadlift focusing traps"),

                // ── CALVES ──
                build("Standing Calf Raise",  "CALVES", "BEGINNER",     "MACHINE",  "Standing calf raise"),
                build("Seated Calf Raise",    "CALVES", "BEGINNER",     "MACHINE",  "Seated calf isolation"),
                build("Leg Press Calf Raise", "CALVES", "BEGINNER",     "MACHINE",  "Calf raises on leg press"),
                build("Donkey Calf Raise",    "CALVES", "INTERMEDIATE", "BODYWEIGHT","Classic donkey calf exercise"),
                build("Single Leg Calf Raise","CALVES", "BEGINNER",     "BODYWEIGHT","Unilateral calf training"),
                build("Jump Rope",            "CALVES", "BEGINNER",     "BODYWEIGHT","Dynamic calf endurance"),

                // ── ABS ──
                build("Plank",                   "ABS", "BEGINNER", "BODYWEIGHT", "Core stability exercise"),
                build("Crunch",                  "ABS", "BEGINNER", "BODYWEIGHT", "Basic ab exercise"),
                build("Mountain Climbers",       "ABS", "BEGINNER", "BODYWEIGHT", "Dynamic core exercise"),
                build("Cable Crunch",            "ABS", "BEGINNER", "CABLE",      "Weighted cable crunch"),

                // ── CARDIO ──
                build("Treadmill",               "CARDIO", "BEGINNER", "MACHINE", "Cardio endurance training")
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