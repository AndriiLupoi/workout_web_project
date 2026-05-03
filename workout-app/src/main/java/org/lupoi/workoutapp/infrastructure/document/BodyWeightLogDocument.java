package org.lupoi.workoutapp.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "body_weight_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyWeightLogDocument {
    @Id
    private String id;

    @Indexed
    private String userId;

    private Double weight;
    private LocalDate date;
}