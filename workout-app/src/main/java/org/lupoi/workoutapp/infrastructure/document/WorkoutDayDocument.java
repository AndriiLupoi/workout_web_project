package org.lupoi.workoutapp.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDayDocument {
    private int dayNumber;
    private int weekNumber;
    private String focus;
    private String intensityType;
    private List<WorkoutExerciseDocument> exercises;
}