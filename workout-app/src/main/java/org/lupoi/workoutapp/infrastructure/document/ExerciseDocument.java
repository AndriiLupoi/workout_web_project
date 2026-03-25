package org.lupoi.workoutapp.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDocument {
    @Id
    private String id;
    private String name;
    private String muscleGroup;
    private String difficulty;
    private String equipmentType;
    private String description;
}