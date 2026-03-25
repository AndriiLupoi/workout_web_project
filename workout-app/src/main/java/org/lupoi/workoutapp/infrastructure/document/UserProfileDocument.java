package org.lupoi.workoutapp.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDocument {
    @Id
    private String id;
    @Indexed
    private String userId;
    private String goal;
    private String level;
    private int workoutsPerWeek;
    private Double currentWeight;
    private Double targetWeight;
    private Double height;
    private Integer age;
}
