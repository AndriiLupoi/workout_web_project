package org.lupoi.workoutapp.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "workout_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanDocument {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String title;
    private String goal;
    private String status;
    private int durationWeeks;
    private List<WorkoutDayDocument> days;
    private LocalDateTime createdAt;
}