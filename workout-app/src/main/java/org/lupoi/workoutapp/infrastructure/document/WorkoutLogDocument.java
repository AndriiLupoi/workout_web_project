package org.lupoi.workoutapp.infrastructure.document;/*
    @author Andrii
    @project workout
    @class WorkoutLogDocument
    @version 1.0.0
    @since 27.04.2026 - 20.43
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "workout_logs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogDocument {

    @Id
    private String id;
    private String userId;
    private String planId;
    private int weekNumber;
    private int dayNumber;
    private List<LoggedExerciseDoc> exercises;
    private String notes;
    private LocalDateTime completedAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoggedExerciseDoc {
        private String exerciseId;
        private String exerciseName;
        private int plannedSets;
        private String plannedReps;
        private Double plannedWeight;
        private int actualSets;
        private String actualReps;
        private Double actualWeight;
        private boolean feltEasy;
        private String notes;
    }
}

