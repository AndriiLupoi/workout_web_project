package org.lupoi.workoutapp.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyWeightLog {
    private String id;
    private String userId;
    private Double weight;
    private LocalDate date;
}