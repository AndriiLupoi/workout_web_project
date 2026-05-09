package org.lupoi.workoutapp.infrastructure.document.workout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseDocument {
    private String exerciseId;
    private String exerciseName;
    private int sets;
    private String reps;
    private int restSeconds;
    private Double plannedWeight;
    private String muscleGroup;
    private String equipmentType;
    private String description;

}